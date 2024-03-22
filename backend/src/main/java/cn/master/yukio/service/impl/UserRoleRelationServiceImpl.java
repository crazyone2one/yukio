package cn.master.yukio.service.impl;

import cn.master.yukio.constants.*;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.request.GlobalUserRoleRelationQueryRequest;
import cn.master.yukio.dto.request.OrganizationUserRoleMemberEditRequest;
import cn.master.yukio.dto.user.UserRoleRelationUserDTO;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.Organization;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.UserRoleMapper;
import cn.master.yukio.mapper.UserRoleRelationMapper;
import cn.master.yukio.service.IOperationLogService;
import cn.master.yukio.service.IUserRoleRelationService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.yukio.constants.InternalUserRole.ADMIN;
import static cn.master.yukio.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.yukio.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.yukio.entity.table.UserTableDef.USER;
import static cn.master.yukio.handler.result.CommonResultCode.USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION;
import static cn.master.yukio.handler.result.SystemResultCode.*;
import static cn.master.yukio.util.ServiceUtils.checkResourceExist;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements IUserRoleRelationService {
    private final IOperationLogService operationLogService;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<String> userRoleIdList, List<User> userList) {
        List<UserRoleRelation> userRoleRelationSaveList = new ArrayList<>();
        for (String userRoleId : userRoleIdList) {
            for (User user : userList) {
                UserRoleRelation userRoleRelation = UserRoleRelation.builder().roleId(userRoleId).userId(user.getId())
                        .createUser(user.getCreateUser())
                        .sourceId(UserRoleScope.SYSTEM)
                        .organizationId(UserRoleScope.SYSTEM)
                        .build();
                userRoleRelationSaveList.add(userRoleRelation);
            }
        }
        mapper.insertBatch(userRoleRelationSaveList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<String> userRoleIdList, User user) {
        List<UserRoleRelation> userRoleRelationSaveList = new ArrayList<>();
        for (String userRoleId : userRoleIdList) {
            UserRoleRelation userRoleRelation = UserRoleRelation.builder().roleId(userRoleId)
                    .userId(user.getId())
                    .createUser(user.getCreateUser())
                    .sourceId(UserRoleScope.SYSTEM)
                    .organizationId(UserRoleScope.SYSTEM)
                    .build();
            userRoleRelationSaveList.add(userRoleRelation);
        }
        mapper.insertBatch(userRoleRelationSaveList);
    }

    @Override
    public Map<String, UserTableResponse> selectGlobalUserRoleAndOrganization(List<String> userIdList) {
        List<UserRoleRelation> userRoleRelationList = selectGlobalRoleByUserIdList(userIdList);
        List<String> userRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).distinct().toList();
        List<String> sourceIdList = userRoleRelationList.stream().map(UserRoleRelation::getSourceId).distinct().toList();
        Map<String, UserRole> userRoleMap = new HashMap<>();
        Map<String, Organization> organizationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userRoleIdList)) {
            userRoleMap = QueryChain.of(UserRole.class).where(USER_ROLE.ID.in(userRoleIdList)
                            .and(USER_ROLE.SCOPE_ID.eq(UserRoleEnum.GLOBAL.toString())))
                    .list().stream().collect(Collectors.toMap(UserRole::getId, item -> item));
        }
        if (CollectionUtils.isNotEmpty(sourceIdList)) {
            organizationMap = QueryChain.of(Organization.class).where(ORGANIZATION.ID.in(sourceIdList)).list()
                    .stream().collect(Collectors.toMap(Organization::getId, item -> item));
        }
        Map<String, UserTableResponse> returnMap = new HashMap<>();
        for (UserRoleRelation userRoleRelation : userRoleRelationList) {
            UserTableResponse userInfo = returnMap.get(userRoleRelation.getUserId());
            if (userInfo == null) {
                userInfo = new UserTableResponse();
                userInfo.setId(userRoleRelation.getUserId());
                returnMap.put(userRoleRelation.getUserId(), userInfo);
            }
            UserRole userRole = userRoleMap.get(userRoleRelation.getRoleId());
            if (userRole != null && StringUtils.equalsIgnoreCase(userRole.getType(), UserRoleScope.SYSTEM)) {
                userInfo.getUserRoleList().add(userRole);
            }
            Organization organization = organizationMap.get(userRoleRelation.getSourceId());
            if (organization != null && !userInfo.getOrganizationList().contains(organization)) {
                userInfo.getOrganizationList().add(organization);
            }
        }
        return returnMap;
    }

    @Override
    public List<UserRoleRelation> selectGlobalRoleByUserIdList(List<String> userIdList) {
        List<UserRole> list = QueryChain.of(UserRole.class).select("id").from(USER_ROLE).where(USER_ROLE.SCOPE_ID.eq("global")).list();
        return QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.in(userIdList)
                .and(USER_ROLE_RELATION.ROLE_ID.in(list.stream().map(UserRole::getId).toList()))).list();
    }

    @Override
    public List<UserRoleRelation> selectGlobalRoleByUserId(String userId) {
        List<UserRole> list = QueryChain.of(UserRole.class).select("id").from(USER_ROLE).where(USER_ROLE.SCOPE_ID.eq("global")).list();
        return QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                .and(USER_ROLE_RELATION.ROLE_ID.in(list.stream().map(UserRole::getId).toList()))).list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserSystemGlobalRole(User user, String operator, List<String> roleList) {
        //更新用户权限
        List<String> deleteRoleList = new ArrayList<>();
        List<UserRoleRelation> saveList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelationList = selectGlobalRoleByUserId(user.getId());
        List<String> userSavedRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).toList();
        //获取要移除的权限
        for (String userSavedRoleId : userSavedRoleIdList) {
            if (!roleList.contains(userSavedRoleId)) {
                deleteRoleList.add(userSavedRoleId);
            }
        }
        //获取要添加的权限
        for (String roleId : roleList) {
            if (!userSavedRoleIdList.contains(roleId)) {
                UserRoleRelation userRoleRelation = UserRoleRelation.builder()
                        .userId(user.getId())
                        .roleId(roleId)
                        .sourceId(UserRoleScope.SYSTEM)
                        .createUser(operator)
                        .organizationId(UserRoleScope.SYSTEM)
                        .build();
                saveList.add(userRoleRelation);
            }
        }
        if (CollectionUtils.isNotEmpty(deleteRoleList)) {
            List<String> deleteIdList = new ArrayList<>();
            userRoleRelationList.forEach(item -> {
                if (deleteRoleList.contains(item.getRoleId())) {
                    deleteIdList.add(item.getId());
                }
            });
            mapper.deleteBatchByIds(deleteIdList);
            operationLogService.batchAdd(getBatchLogs(deleteRoleList, user, "updateUser", operator, OperationLogType.DELETE.name()));
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            saveList.forEach(item -> mapper.insert(item));
            operationLogService.batchAdd(getBatchLogs(saveList.stream().map(UserRoleRelation::getRoleId).toList(), user, "updateUser", operator, OperationLogType.ADD.name()));
        }
    }

    @Override
    public List<UserRoleRelation> selectByUserId(String id) {
        return queryChain().where(USER_ROLE_RELATION.USER_ID.eq(id)).list();
    }

    @Override
    public void removeByUserIdList(List<String> userIdList) {
        mapper.deleteByQuery(queryChain().where(USER_ROLE_RELATION.USER_ID.in(userIdList)));
    }

    @Override
    public void deleteByRoleId(String roleId) {
        List<UserRoleRelation> userRoleRelations = getByRoleId(roleId);
        userRoleRelations.forEach(userRoleRelation -> checkAdminPermissionRemove(userRoleRelation.getUserId(), userRoleRelation.getRoleId()));
        QueryChain<UserRoleRelation> queryChain = queryChain().where(USER_ROLE_RELATION.ROLE_ID.eq(roleId));
        mapper.deleteByQuery(queryChain);
    }

    private void checkAdminPermissionRemove(String userId, String roleId) {
        if (StringUtils.equals(roleId, ADMIN.getValue()) && StringUtils.equals(userId, ADMIN.getValue())) {
            throw new MSException(USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION);
        }
    }

    @Override
    public List<UserRoleRelation> getByRoleId(String roleId) {
        return queryChain().where(USER_ROLE_RELATION.ROLE_ID.eq(roleId)).list();
    }

    @Override
    public List<String> getUserIdByRoleId(String roleId) {
        List<UserRoleRelation> userRoleRelations = getByRoleId(roleId);
        return userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
    }

    @Override
    public List<UserRoleRelation> getUserIdAndSourceIdByUserIds(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>(0);
        }
        return queryChain().select(USER_ROLE_RELATION.SOURCE_ID, USER_ROLE_RELATION.USER_ID, USER_ROLE_RELATION.ROLE_ID)
                .where(USER_ROLE_RELATION.USER_ID.in(userIds)).list();
    }

    @Override
    public Page<UserRoleRelationUserDTO> page(GlobalUserRoleRelationQueryRequest request) {
        QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class)
                .select(USER_ROLE_RELATION.ID, USER.ID.as("userId"), USER.NAME, USER.EMAIL, USER.PHONE)
                .from(USER_ROLE_RELATION)
                .innerJoin(USER).on(USER.ID.eq(USER_ROLE_RELATION.USER_ID)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(request.getRoleId()))
                        .and(USER.NAME.like(request.getKeyword())
                                .or(USER.EMAIL.like(request.getKeyword()))))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc());
        Page<UserRoleRelationUserDTO> userRoleRelationUsersPage = mapper.paginateAs(Page.of(request.getCurrent(), request.getPageSize()), queryChain, UserRoleRelationUserDTO.class);
        //List<UserRoleRelationUserDTO> records = userRoleRelationUsersPage.getRecords();
        UserRole userRole = userRoleMapper.selectOneById(request.getRoleId());
        checkSystemUserGroup(userRole);
        checkGlobalUserRole(userRole);
        return userRoleRelationUsersPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        UserRole userRole = getUserRole(id);
        checkResourceExist(userRole, "permission.system_user_role.name");
        UserRoleRelation userRoleRelation = mapper.selectOneById(id);
        checkSystemUserGroup(userRole);
        checkGlobalUserRole(userRole);
        mapper.deleteById(id);
        List<UserRoleRelation> userRoleRelations = queryChain().where(USER_ROLE_RELATION.SOURCE_ID.eq(UserRoleScope.SYSTEM)
                .and(USER_ROLE_RELATION.USER_ID.eq(userRoleRelation.getUserId()))).list();
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            throw new MSException(GLOBAL_USER_ROLE_LIMIT);
        }

    }

    private UserRole getUserRole(String id) {
        UserRoleRelation userRoleRelation = mapper.selectOneById(id);
        return userRoleRelation == null ? null : userRoleMapper.selectOneById(userRoleRelation.getRoleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(OrganizationUserRoleMemberEditRequest request) {
        String removeUserId = request.getUserIds().get(0);
        checkMemberParam(removeUserId, request.getUserRoleId());
        //检查移除的是不是管理员
        if (com.alibaba.excel.util.StringUtils.equals(request.getUserRoleId(), InternalUserRole.ORG_ADMIN.getValue())) {
            List<UserRoleRelation> userRoleRelations = queryChain()
                    .where(USER_ROLE_RELATION.USER_ID.ne(removeUserId)
                    .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue()))
                    .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId()))).list();
            if (userRoleRelations.isEmpty()) {
                throw new MSException(Translator.get("keep_at_least_one_administrator"));
            }
        }
        // 移除组织-用户组的成员, 若成员只存在该组织下唯一用户组, 则提示不能移除
        QueryChain<UserRoleRelation> queryChain = queryChain()
                .where(USER_ROLE_RELATION.USER_ID.eq(removeUserId)
                .and(USER_ROLE_RELATION.ROLE_ID.ne(request.getUserRoleId()))
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())));
        if (queryChain.count() == 0) {
            throw new MSException(Translator.get("org_at_least_one_user_role_require"));
        }
        queryChain.clear();
        QueryChain<UserRoleRelation> delete = queryChain()
                .where(USER_ROLE_RELATION.USER_ID.eq(removeUserId)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(request.getUserRoleId()))
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())));
        mapper.deleteByQuery(delete);
    }

    private void checkMemberParam(String userId, String roleId) {
        if (!QueryChain.of(User.class).where(USER.ID.eq(userId)).exists()) {
            throw new MSException(Translator.get("user_not_exist"));
        }
        if (!QueryChain.of(UserRole.class).where(USER_ROLE.ID.eq(roleId)).exists()) {
            throw new MSException(Translator.get("user_role_not_exist"));
        }
    }

    private void checkSystemUserGroup(UserRole userRole) {
        if (!StringUtils.equalsIgnoreCase(userRole.getType(), UserRoleType.SYSTEM.name())) {
            throw new MSException(GLOBAL_USER_ROLE_RELATION_SYSTEM_PERMISSION);
        }
    }

    private void checkGlobalUserRole(UserRole userRole) {
        if (!StringUtils.equals(userRole.getScopeId(), UserRoleScope.GLOBAL)) {
            throw new MSException(GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    private List<LogDTO> getBatchLogs(@Valid @NotEmpty List<String> userRoleId,
                                      @Valid User user,
                                      @Valid @NotEmpty String operationMethod,
                                      @Valid @NotEmpty String operator,
                                      @Valid @NotEmpty String operationType) {
        List<LogDTO> logs = new ArrayList<>();
        List<UserRole> userRoleList = QueryChain.of(UserRole.class).select("id").where(USER_ROLE.ID.in(userRoleId)).list();
        userRoleList.forEach(userRole -> {
            LogDTO log = new LogDTO();
            log.setProjectId(OperationLogConstants.SYSTEM);
            log.setOrganizationId(OperationLogConstants.SYSTEM);
            log.setType(operationType);
            log.setCreateUser(operator);
            log.setModule(OperationLogModule.SETTING_SYSTEM_USER_SINGLE);
            log.setMethod(operationMethod);
            log.setCreateTime(LocalDateTime.now());
            log.setSourceId(user.getId());
            log.setContent(user.getName() + StringUtils.SPACE
                    + Translator.get(StringUtils.lowerCase(operationType)) + StringUtils.SPACE
                    + Translator.get("permission.project_group.name") + StringUtils.SPACE
                    + userRole.getName());
            log.setOriginalValue(JsonUtils.toJsonByte(userRole));
            logs.add(log);
        });
        return logs;
    }
}
