package cn.master.yukio.service.impl;

import cn.master.yukio.constants.*;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.organization.*;
import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.Organization;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.OrganizationMapper;
import cn.master.yukio.mapper.UserMapper;
import cn.master.yukio.mapper.UserRoleRelationMapper;
import cn.master.yukio.service.IOperationLogService;
import cn.master.yukio.service.IOrganizationService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.yukio.entity.table.ProjectTableDef.PROJECT;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.yukio.entity.table.UserTableDef.USER;

/**
 * 组织 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements IOrganizationService {
    private final UserMapper userMapper;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final IOperationLogService operationLogService;

    private static final String ADD_MEMBER_PATH = "/system/organization/add-member";
    private static final String REMOVE_MEMBER_PATH = "/system/organization/remove-member";
    public static final Integer DEFAULT_REMAIN_DAY_COUNT = 30;
    private static final Long DEFAULT_ORGANIZATION_NUM = 100001L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrg(OrganizationDTO organizationDTO) {
        checkOrganizationNotExist(organizationDTO.getId());
        checkOrganizationExist(organizationDTO);
        mapper.update(organizationDTO);
        // 新增的组织管理员ID
        List<String> addOrgAdmins = organizationDTO.getUserIds();
        // 旧的组织管理员ID
        List<String> oldOrgAdmins = getOrgAdminIds(organizationDTO.getId());
        // 需要新增组织管理员ID
        List<String> addIds = addOrgAdmins.stream().filter(addOrgAdmin -> !oldOrgAdmins.contains(addOrgAdmin)).toList();
        // 需要删除的组织管理员ID
        List<String> deleteIds = oldOrgAdmins.stream().filter(oldOrgAdmin -> !addOrgAdmins.contains(oldOrgAdmin)).toList();
        // 新增组织管理员
        if (!addIds.isEmpty()) {
            addIds.forEach(addId -> {
                // 添加组织管理员
                createAdmin(addId, organizationDTO.getId(), organizationDTO.getUpdateUser());
            });
        }
        // 删除组织管理员
        if (!deleteIds.isEmpty()) {
            QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class)
                    .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationDTO.getId())
                            .and(USER_ROLE_RELATION.USER_ID.in(deleteIds))
                            .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue())));
            userRoleRelationMapper.deleteByQuery(queryChain);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateName(OrganizationDTO organizationDTO) {
        checkOrganizationNotExist(organizationDTO.getId());
        checkOrganizationExist(organizationDTO);
        mapper.update(organizationDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(OrganizationDeleteRequest organizationDeleteRequest) {
        // 默认组织不允许删除
        checkOrgDefault(organizationDeleteRequest.getOrganizationId());
        checkOrganizationNotExist(organizationDeleteRequest.getOrganizationId());
        Organization organization = mapper.selectOneById(organizationDeleteRequest.getOrganizationId());
        organization.setDeleteUser(organizationDeleteRequest.getDeleteUserId());
        organization.setDeleteTime(organizationDeleteRequest.getDeleteTime());
        mapper.delete(organization);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recover(String id) {
        checkOrganizationNotExist(id);
        Organization organization = UpdateEntity.of(Organization.class, id);
        organization.setDeleted(false);
        mapper.update(organization);
    }

    @Override
    public void enable(String id) {
        checkOrganizationNotExist(id);
        Organization organization = UpdateEntity.of(Organization.class, id);
        organization.setEnable(true);
        mapper.update(organization);
    }

    @Override
    public void disable(String id) {
        checkOrganizationNotExist(id);
        Organization organization = UpdateEntity.of(Organization.class, id);
        organization.setEnable(false);
        mapper.update(organization);
    }

    @Override
    public Page<UserExtendDTO> getMemberListBySystem(OrganizationRequest request) {
        QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class).select("temp.*",
                "max(if(temp.role_id = 'org_admin', true, false)) as adminFlag",
                "min(temp.memberTime) as groupTime"
        ).from(
                QueryChain.of(UserRoleRelation.class)
                        .select(USER_ROLE_RELATION.ROLE_ID, USER_ROLE_RELATION.CREATE_TIME.as("memberTime"), USER.ALL_COLUMNS)
                        .from(USER_ROLE_RELATION)
                        .join(USER).on(USER_ROLE_RELATION.USER_ID.eq(USER.ID))
                        .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())
                                .and(USER.NAME.like(request.getKeyword())
                                        .or(USER.EMAIL.like(request.getKeyword()))
                                        .or(USER.PHONE.like(request.getKeyword()))))
                        .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc()).as("temp")
        ).groupBy("temp.id").orderBy("adminFlag desc", "groupTime desc");
        return mapper.paginateAs(Page.of(request.getCurrent(), request.getPageSize()), queryChain, UserExtendDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMemberBySystem(OrganizationMemberRequest request, String createUserId) {
        List<LogDTO> logs = new ArrayList<>();
        OrganizationMemberBatchRequest batchRequest = new OrganizationMemberBatchRequest();
        batchRequest.setOrganizationIds(List.of(request.getOrganizationId()));
        batchRequest.setUserIds(request.getUserIds());
        addMemberBySystem(batchRequest, createUserId);
        // 添加日志
        List<User> users = userMapper.selectListByIds(batchRequest.getUserIds());
        List<String> nameList = users.stream().map(User::getName).toList();
        setLog(request.getOrganizationId(), createUserId, OperationLogType.ADD.name(), Translator.get("add") + Translator.get("organization_member_log") + ": " + StringUtils.join(nameList, ","), ADD_MEMBER_PATH, null, null, logs);
        operationLogService.batchAdd(logs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMemberBySystem(OrganizationMemberBatchRequest batchRequest, String createUserId) {
        checkOrgExistByIds(batchRequest.getOrganizationIds());
        Map<String, User> userMap = checkUserExist(batchRequest.getUserIds());
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        batchRequest.getOrganizationIds().forEach(organizationId -> {
            for (String userId : batchRequest.getUserIds()) {
                if (userMap.get(userId) == null) {
                    throw new MSException(Translator.get("user.not.exist") + ", id: " + userId);
                }
                //组织用户关系已存在, 不再重复添加
                long count = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId).and(USER_ROLE_RELATION.USER_ID.eq(userId))).count();
                if (count > 0) {
                    continue;
                }
                UserRoleRelation userRoleRelation = UserRoleRelation.builder()
                        .userId(userId)
                        .sourceId(organizationId)
                        .roleId(InternalUserRole.ORG_MEMBER.getValue())
                        .createUser(createUserId)
                        .organizationId(organizationId)
                        .build();
                userRoleRelations.add(userRoleRelation);
            }
        });
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(String organizationId, String userId, String currentUser) {
        List<LogDTO> logs = new ArrayList<>();
        checkOrgExistById(organizationId);
        boolean exists = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.ne(userId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId))
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue()))).exists();
        if (!exists) {
            throw new MSException(Translator.get("keep_at_least_one_administrator"));
        }
        //删除组织下项目与成员的关系
        List<String> projectIds = getProjectIds(organizationId);
        if (CollectionUtils.isNotEmpty(projectIds)) {
            QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds)
                    .and(USER_ROLE_RELATION.USER_ID.eq(userId)));
            userRoleRelationMapper.deleteByQuery(queryChain);
        }
        //删除组织与成员的关系
        QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)
                .and(USER_ROLE_RELATION.USER_ID.eq(userId)));
        userRoleRelationMapper.deleteByQuery(queryChain);
        // 操作记录
        User user = userMapper.selectOneById(userId);
        setLog(organizationId, currentUser, OperationLogType.DELETE.name(), Translator.get("delete") + Translator.get("organization_member_log") + ": " + user.getName(), REMOVE_MEMBER_PATH, user, null, logs);
        operationLogService.batchAdd(logs);
    }

    @Override
    public OrganizationDTO getDefault() {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        List<Organization> organizations = this.queryChain().where(ORGANIZATION.NUM.eq(100001L)).list();
        Organization organization = organizations.get(0);
        BeanUtils.copyProperties(organization, organizationDTO);
        return organizationDTO;
    }

    private List<String> getProjectIds(String organizationId) {
        List<Project> projects = QueryChain.of(Project.class).where(PROJECT.ORGANIZATION_ID.eq(organizationId)).list();
        return CollectionUtils.isNotEmpty(projects) ? projects.stream().map(Project::getId).collect(Collectors.toList()) : new ArrayList<>();
    }

    private void checkOrgExistById(String organizationId) {
        if (Objects.isNull(mapper.selectOneById(organizationId))) {
            throw new MSException(Translator.get("organization_not_exist"));
        }
    }

    private void setLog(String organizationId, String createUser, String type, String content, String path, Object originalValue, Object modifiedValue, List<LogDTO> logs) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                organizationId,
                createUser,
                type,
                OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                content);
        dto.setPath(path);
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setOriginalValue(JsonUtils.toJsonByte(originalValue));
        dto.setModifiedValue(JsonUtils.toJsonByte(modifiedValue));
        logs.add(dto);
    }

    private Map<String, User> checkUserExist(List<String> userIds) {
        List<User> users = userMapper.selectListByIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            throw new MSException(Translator.get("user.not.exist"));
        }
        return users.stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    private void checkOrgExistByIds(List<String> organizationIds) {
        List<Organization> organizations = mapper.selectListByIds(organizationIds);
        if (organizations.size() < organizationIds.size()) {
            throw new MSException(Translator.get("organization_not_exist"));
        }
    }

    private void checkOrgDefault(String organizationId) {
        Organization organization = mapper.selectOneById(organizationId);
        if (organization.getNum().equals(DEFAULT_ORGANIZATION_NUM)) {
            throw new MSException(Translator.get("default_organization_not_allow_delete"));
        }
    }

    private void createAdmin(String memberId, String organizationId, String createUser) {
        UserRoleRelation userRoleRelation = UserRoleRelation.builder()
                .userId(memberId)
                .roleId(InternalUserRole.ORG_ADMIN.getValue())
                .sourceId(organizationId)
                .createUser(createUser)
                .organizationId(organizationId)
                .build();
        userRoleRelationMapper.insert(userRoleRelation);
    }

    private List<String> getOrgAdminIds(String organizationId) {
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue())))
                .list();
        return userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
    }

    private void checkOrganizationExist(OrganizationDTO organizationDTO) {
        boolean exists = this.queryChain().where(ORGANIZATION.NAME.eq(organizationDTO.getName())
                .and(ORGANIZATION.ID.ne(organizationDTO.getId()))).exists();
        if (exists) {
            throw new MSException(Translator.get("organization_name_already_exists"));
        }
    }

    private void checkOrganizationNotExist(String id) {
        if (Objects.isNull(mapper.selectOneById(id))) {
            throw new MSException(Translator.get("organization_not_exist"));
        }
    }
}