package cn.master.yukio.service.impl;

import cn.master.yukio.config.PermissionCache;
import cn.master.yukio.constants.InternalUserRole;
import cn.master.yukio.constants.UserRoleEnum;
import cn.master.yukio.constants.UserRoleScope;
import cn.master.yukio.constants.UserRoleType;
import cn.master.yukio.dto.permission.Permission;
import cn.master.yukio.dto.permission.PermissionDefinitionItem;
import cn.master.yukio.dto.request.OrganizationUserRoleMemberRequest;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.entity.UserRolePermission;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.UserRoleMapper;
import cn.master.yukio.mapper.UserRolePermissionMapper;
import cn.master.yukio.service.IUserRolePermissionService;
import cn.master.yukio.service.IUserRoleRelationService;
import cn.master.yukio.service.IUserRoleService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.yukio.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.yukio.entity.table.UserTableDef.USER;
import static cn.master.yukio.handler.result.CommonResultCode.INTERNAL_USER_ROLE_PERMISSION;
import static cn.master.yukio.handler.result.SystemResultCode.*;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {
    private final UserRolePermissionMapper userRolePermissionMapper;
    private final IUserRolePermissionService userRolePermissionService;
    private final IUserRoleRelationService userRoleRelationService;
    private final PermissionCache permissionCache;

    @Override
    public void checkRoleIsGlobalAndHaveMember(List<String> roleIdList, boolean isSystem) {
        List<UserRole> globalRoleList = this.queryChain().select(USER_ROLE.ID).from(USER_ROLE)
                .where(USER_ROLE.ID.in(roleIdList)
                        .and(USER_ROLE.TYPE.eq("SYSTEM"))
                        .and(USER_ROLE.SCOPE_ID.eq("GLOBAL"))).list();
        if (globalRoleList.size() != roleIdList.size()) {
            throw new MSException(Translator.get("role.not.global"));
        }
    }

    @Override
    public List<UserRole> selectByUserRoleRelations(List<UserRoleRelation> userRoleRelations) {
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            List<String> userRoleIds = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
            return queryChain().where(USER_ROLE.ID.in(userRoleIds)).list();
        }
        return Collections.emptyList();
    }

    @Override
    public List<UserRole> listByOrgId(String organizationId) {
        List<UserRole> userRoles = queryChain().where(USER_ROLE.TYPE.eq(UserRoleType.ORGANIZATION.name())
                        .and(USER_ROLE.SCOPE_ID.in(Arrays.asList(organizationId, UserRoleEnum.GLOBAL.toString()))))
                .orderBy(USER_ROLE.CREATE_TIME.desc()).list();
        userRoles.sort(Comparator.comparing(UserRole::getInternal).reversed());
        return userRoles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRole add(UserRole userRole) {
        userRole.setInternal(false);
        userRole.setType(UserRoleType.ORGANIZATION.name());
        checkNewRoleExist(userRole);
        mapper.insert(userRole);
        return userRole;
    }

    @Override
    public void checkNewRoleExist(UserRole userRole) {
        boolean exists = queryChain().where(USER_ROLE.NAME.eq(userRole.getName())
                .and(USER_ROLE.SCOPE_ID.in(Arrays.asList(userRole.getScopeId(), UserRoleEnum.GLOBAL.toString())))
                .and(USER_ROLE.TYPE.eq(userRole.getType()))
                .and(USER_ROLE.ID.ne(userRole.getId()))).exists();
        if (exists) {
            throw new MSException(Translator.get("user_role_exist"));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRole updateItem(UserRole userRole) {
        UserRole oldRole = mapper.selectOneById(userRole.getId());
        checkOrgUserRole(oldRole);
        checkGlobalUserRole(oldRole);
        userRole.setType(UserRoleType.ORGANIZATION.name());
        checkNewRoleExist(userRole);
        mapper.update(userRole);
        return userRole;
    }

    @Override
    public void checkGlobalUserRole(UserRole userRole) {
        if (StringUtils.equals(userRole.getScopeId(), UserRoleEnum.GLOBAL.toString())) {
            throw new MSException(NO_GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        UserRole userRole = mapper.selectOneById(id);
        checkOrgUserRole(userRole);
        checkGlobalUserRole(userRole);
        deleteItem(userRole, InternalUserRole.ORG_MEMBER.getValue(), SessionUtils.getUserId(), userRole.getScopeId());
    }

    @Override
    public void checkInternalUserRole(UserRole userRole) {
        if (BooleanUtils.isTrue(userRole.getInternal())) {
            throw new MSException(INTERNAL_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = mapper.selectOneById(id);
        if (userRole == null) {
            throw new MSException(Translator.get("user_role_not_exist"));
        }
        checkOrgUserRole(userRole);
        return getPermissionSetting(userRole);
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(UserRole userRole) {
        // 获取该用户组拥有的权限
        Set<String> permissionIds = userRolePermissionService.getPermissionIdSetByRoleId(userRole.getId());
        // 获取所有的权限
        List<PermissionDefinitionItem> permissionDefinition = permissionCache.getPermissionDefinition();
        // 深拷贝
        permissionDefinition = JsonUtils.parseArray(JsonUtils.toJsonString(permissionDefinition), PermissionDefinitionItem.class);

        // 过滤该用户组级别的菜单，例如系统级别
        permissionDefinition = permissionDefinition.stream()
                .filter(item -> StringUtils.equals(item.getType(), userRole.getType()))
                .toList();
        for (PermissionDefinitionItem firstLevel : permissionDefinition) {
            List<PermissionDefinitionItem> children = firstLevel.getChildren();
            boolean allCheck = true;
            for (PermissionDefinitionItem secondLevel : children) {
                List<Permission> permissions = secondLevel.getPermissions();
                secondLevel.setName(Translator.get(secondLevel.getName()));
                if (CollectionUtils.isEmpty(permissions)) {
                    continue;
                }
                boolean secondAllCheck = true;
                for (Permission p : permissions) {
                    if (StringUtils.isNotBlank(p.getName())) {
                        // 有 name 字段翻译 name 字段
                        p.setName(Translator.get(p.getName()));
                    } else {
                        p.setName(translateDefaultPermissionName(p));
                    }
                    if (permissionIds.contains(p.getId())) {
                        p.setEnable(true);
                    } else {
                        // 如果权限有未勾选，则二级菜单设置为未勾选
                        p.setEnable(false);
                        secondAllCheck = false;
                    }
                }
                secondLevel.setEnable(secondAllCheck);
                if (!secondAllCheck) {
                    // 如果二级菜单有未勾选，则一级菜单设置为未勾选
                    allCheck = false;
                }
            }
            firstLevel.setEnable(allCheck);
        }
        return permissionDefinition;
    }

    @Override
    public List<UserRole> globalList() {
        List<UserRole> userRoles = queryChain().where(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL)).list();
        // 先按照类型排序，再按照创建时间排序
        userRoles.sort(Comparator.comparingInt(this::getTypeOrder)
                .thenComparingInt(item -> getInternal(item.getInternal()))
                .thenComparing(UserRole::getCreateTime));
        return userRoles;
    }

    @Override
    public void checkSystemUserGroup(UserRole userRole) {
        if (!StringUtils.equalsIgnoreCase(userRole.getType(), UserRoleType.SYSTEM.name())) {
            throw new MSException(GLOBAL_USER_ROLE_RELATION_SYSTEM_PERMISSION);
        }
    }

    @Override
    public Page<User> listMember(OrganizationUserRoleMemberRequest request) {
        return QueryChain.of(UserRoleRelation.class).select(USER.ALL_COLUMNS)
                .from(USER_ROLE_RELATION)
                .leftJoin(USER).on(USER_ROLE_RELATION.USER_ID.eq(USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(request.getUserRoleId()))
                        .and(USER.NAME.like(request.getKeyword())
                                .or(USER.EMAIL.like(request.getKeyword()))
                                .or(USER.PHONE.like(request.getKeyword()))))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc())
                .pageAs(Page.of(request.getCurrent(), request.getPageSize()), User.class);
    }

    private int getInternal(Boolean internal) {
        return BooleanUtils.isTrue(internal) ? 0 : 1;
    }

    private int getTypeOrder(UserRole userRole) {
        Map<String, Integer> typeOrderMap = new HashMap<>(3) {{
            put(UserRoleType.SYSTEM.name(), 1);
            put(UserRoleType.ORGANIZATION.name(), 2);
            put(UserRoleType.PROJECT.name(), 3);
        }};
        return typeOrderMap.getOrDefault(userRole.getType(), 0);
    }

    private String translateDefaultPermissionName(Permission p) {
        if (StringUtils.isNotBlank(p.getName())) {
            p.getName();
        }
        String[] idSplit = p.getId().split(":");
        String permissionKey = idSplit[idSplit.length - 1];
        Map<String, String> translationMap = new HashMap<>() {{
            put("READ", "permission.read");
            put("READ+ADD", "permission.add");
            put("READ+UPDATE", "permission.edit");
            put("READ+DELETE", "permission.delete");
            put("READ+IMPORT", "permission.import");
            put("READ+RECOVER", "permission.recover");
            put("READ+EXPORT", "permission.export");
            put("READ+EXECUTE", "permission.execute");
            put("READ+DEBUG", "permission.debug");
        }};
        return Translator.get(translationMap.get(permissionKey));
    }

    private void deleteItem(UserRole userRole, String defaultRoleId, String currentUserId, String orgId) {
        String id = userRole.getId();
        checkInternalUserRole(userRole);
        // 删除用户组的权限设置
        userRolePermissionMapper.deleteByQuery(QueryChain.of(UserRolePermission.class).where(USER_ROLE_PERMISSION.ROLE_ID.eq(id)));
        mapper.deleteById(id);
        checkOneLimitRole(id, defaultRoleId, currentUserId, orgId);
        // 删除用户组与用户的关联关系
        userRoleRelationService.deleteByRoleId(id);
    }

    /**
     * 删除用户组时校验必须要有一个用户组.没有的话，添加系统成员，组织成员，项目成员用户组
     *
     * @param roleId
     * @param defaultRoleId
     * @param orgId
     * @parvam currentUserId
     */
    private void checkOneLimitRole(String roleId, String defaultRoleId, String currentUserId, String orgId) {
        // 查询要删除的用户组关联的用户ID
        List<String> userIds = userRoleRelationService.getUserIdByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        // 查询用户列表与所有用户组的关联关系，并分组（UserRoleRelation 中只有 userId 和 sourceId）
        Map<String, List<UserRoleRelation>> userRoleRelationMap = userRoleRelationService
                .getUserIdAndSourceIdByUserIds(userIds)
                .stream()
                .collect(Collectors.groupingBy(i -> i.getUserId() + i.getSourceId()));
        List<UserRoleRelation> addRelations = new ArrayList<>();
        userRoleRelationMap.forEach((groupId, relations) -> {
            // 如果当前用户组只有一个用户，并且就是要删除的用户组，则添加组织成员等默认用户组
            if (relations.size() == 1 && StringUtils.equals(relations.get(0).getRoleId(), roleId)) {
                UserRoleRelation relation = UserRoleRelation.builder()
                        .userId(relations.get(0).getUserId())
                        .sourceId(relations.get(0).getSourceId())
                        .roleId(defaultRoleId)
                        .createUser(currentUserId)
                        .organizationId(orgId)
                        .build();
                addRelations.add(relation);
            }
        });
        userRoleRelationService.saveBatch(addRelations);
    }

    private void checkOrgUserRole(UserRole userRole) {
        if (!UserRoleType.ORGANIZATION.name().equals(userRole.getType())) {
            throw new MSException(NO_ORG_USER_ROLE_PERMISSION);
        }
    }

}
