package cn.master.yukio.service.impl;

import cn.master.yukio.constants.*;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.Organization;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.UserRoleRelationMapper;
import cn.master.yukio.service.IOperationLogService;
import cn.master.yukio.service.IUserRoleRelationService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.Translator;
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
import static cn.master.yukio.handler.result.CommonResultCode.USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION;

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
        return queryChain().select(USER_ROLE_RELATION.SOURCE_ID,USER_ROLE_RELATION.USER_ID,USER_ROLE_RELATION.ROLE_ID)
                .where(USER_ROLE_RELATION.USER_ID.in(userIds)).list();
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
