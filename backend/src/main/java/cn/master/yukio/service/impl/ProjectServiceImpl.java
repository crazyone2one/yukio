package cn.master.yukio.service.impl;

import cn.master.yukio.constants.*;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.project.*;
import cn.master.yukio.dto.request.ProjectAddMemberBatchRequest;
import cn.master.yukio.dto.user.UserDTO;
import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.*;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.OrganizationMapper;
import cn.master.yukio.mapper.ProjectMapper;
import cn.master.yukio.mapper.ProjectVersionMapper;
import cn.master.yukio.mapper.UserRoleRelationMapper;
import cn.master.yukio.service.IOperationLogService;
import cn.master.yukio.service.IProjectService;
import cn.master.yukio.service.IUserService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.ServiceUtils;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.yukio.entity.table.ProjectTableDef.PROJECT;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.yukio.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.yukio.entity.table.UserTableDef.USER;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    private final IUserService userService;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final IOperationLogService operationLogService;
    private final ProjectVersionMapper projectVersionMapper;
    private final OrganizationMapper organizationMapper;

    private final static String PREFIX = "/system/project";
    private final static String ADD_PROJECT = PREFIX + "/add";
    private final static String UPDATE_PROJECT = PREFIX + "/update";
    private final static String REMOVE_PROJECT_MEMBER = PREFIX + "/remove-member/";
    private final static String ADD_MEMBER = PREFIX + "/add-member";
    public static final Integer DEFAULT_REMAIN_DAY_COUNT = 30;
    public static final String API_TEST = "apiTest";
    public static final String UI_TEST = "uiTest";
    public static final String LOAD_TEST = "loadTest";

    @Override
    public ProjectDTO add(AddProjectRequest addProjectDTO, String createUser, String path, String module) {
        ProjectDTO projectDTO = new ProjectDTO();
        Project project = Project.builder().build();
        BeanUtils.copyProperties(addProjectDTO, project);
        checkProjectExistByName(project);
        project.setCreateUser(createUser);
        project.setUpdateUser(createUser);
        //BeanUtils.copyProperties(project, projectDTO);
        projectDTO.setOrganizationName(QueryChain.of(Organization.class).where(ORGANIZATION.ID.eq(project.getOrganizationId())).one().getName());
        if (CollectionUtils.isNotEmpty(addProjectDTO.getModuleIds())) {
            project.setModuleSetting(addProjectDTO.getModuleIds());
            projectDTO.setModuleIds(addProjectDTO.getModuleIds());
        }
        mapper.insert(project);
        addProjectVersion(project);
        ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
        memberRequest.setProjectIds(List.of(project.getId()));
        memberRequest.setUserIds(addProjectDTO.getUserIds());
        addProjectAdmin(memberRequest, createUser, path, OperationLogType.ADD.name(), Translator.get("add"), module);
        BeanUtils.copyProperties(project, projectDTO);
        return projectDTO;
    }

    private void addProjectVersion(Project project) {
        ProjectVersion projectVersion = ProjectVersion.builder()
                .projectId(project.getId())
                .name("v1.0")
                .description(project.getDescription())
                .status("open")
                .latest(true)
                .publishTime(LocalDateTime.now())
                .startTime(LocalDateTime.now())
                .createUser(SessionUtils.getUserId())
                .build();
        boolean exists = QueryChain.of(ProjectVersion.class).where(ProjectVersion::getProjectId).eq(projectVersion.getProjectId())
                .and(ProjectVersion::getName).eq(projectVersion.getName()).exists();
        if (exists) {
            throw new MSException("当前版本已经存在");
        }
        projectVersionMapper.insert(projectVersion);
    }

    private void addProjectAdmin(ProjectAddMemberBatchRequest request, String createUser, String path, String type, String content, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        List<String> projectIds = request.getProjectIds();
        projectIds.forEach(projectId -> {
            Project project = mapper.selectOneById(projectId);
            Map<String, String> nameMap = addUserPre(request, createUser, path, module, projectId, project);
            request.getUserIds().forEach(userId -> {
                List<UserRoleRelation> userRoleRelations1 = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId))
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).list();
                if (CollectionUtils.isEmpty(userRoleRelations1)) {
                    UserRoleRelation adminRole = UserRoleRelation.builder()
                            .userId(userId)
                            .roleId(InternalUserRole.PROJECT_ADMIN.getValue())
                            .sourceId(projectId)
                            .createUser(createUser)
                            .organizationId(project.getOrganizationId())
                            .build();
                    userRoleRelations.add(adminRole);
                    String logProjectId = OperationLogConstants.SYSTEM;
                    if (StringUtils.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                        logProjectId = OperationLogConstants.ORGANIZATION;
                    }
                    LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), adminRole.getId(), createUser, type, module, content + Translator.get("project_admin") + ": " + nameMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
        });
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
        operationLogService.batchAdd(logDTOList);
    }

    private Map<String, String> addUserPre(ProjectAddMemberBatchRequest request, String createUser, String path, String module, String projectId, Project project) {
        checkProjectNotExist(projectId);
        List<User> users = QueryChain.of(User.class).where(USER.ID.in(request.getUserIds())).list();
        if (request.getUserIds().size() != users.size()) {
            throw new MSException(Translator.get("user_not_exist"));
        }
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(User::getId, User::getName));
        checkOrgRoleExit(request.getUserIds(), project.getOrganizationId(), createUser, userMap, path, module);
        return userMap;
    }

    private void checkOrgRoleExit(List<String> userId, String orgId, String createUser, Map<String, String> nameMap, String path, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.in(userId)
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(orgId))).list();
        //把用户id放到一个新的list
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        if (CollectionUtils.isNotEmpty(userId)) {
            List<UserRoleRelation> userRoleRelation = new ArrayList<>();
            userId.forEach(id -> {
                if (!orgUserIds.contains(id)) {
                    UserRoleRelation memberRole = UserRoleRelation.builder()
                            .userId(id)
                            .roleId(InternalUserRole.ORG_MEMBER.getValue())
                            .sourceId(orgId)
                            .createUser(createUser)
                            .organizationId(orgId)
                            .build();
                    userRoleRelation.add(memberRole);
                    LogDTO logDTO = new LogDTO(orgId, orgId, memberRole.getId(), createUser, OperationLogType.ADD.name(), module, Translator.get("add") + Translator.get("organization_member") + ": " + nameMap.get(id));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
            if (CollectionUtils.isNotEmpty(userRoleRelation)) {
                userRoleRelations.forEach(userRoleRelationMapper::insert);
            }
        }
        operationLogService.batchAdd(logDTOList);
    }

    private void setLog(LogDTO dto, String path, String method, List<LogDTO> logDTOList) {
        dto.setPath(path);
        dto.setMethod(method);
        dto.setOriginalValue(JsonUtils.toJsonByte(StringUtils.EMPTY));
        logDTOList.add(dto);
    }

    private void checkProjectNotExist(String projectId) {
        if (Objects.isNull(mapper.selectOneById(projectId))) {
            throw new MSException(Translator.get("project_is_not_exist"));
        }
    }

    private void checkProjectExistByName(Project project) {
        boolean exists = queryChain().where(PROJECT.NAME.eq(project.getName())
                .and(PROJECT.ORGANIZATION_ID.eq(project.getOrganizationId()))
                .and(PROJECT.ID.ne(project.getId()))).exists();
        if (exists) {
            throw new MSException(Translator.get("project_name_already_exists"));
        }
    }

    @Override
    public ProjectDTO add(AddProjectRequest request, String createUser) {
        return add(request, createUser, ADD_PROJECT, OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }

    @Override
    public Page<ProjectDTO> getProjectPageList(ProjectRequest request) {
        QueryChain<Project> queryChain = QueryChain.of(Project.class)
                .select(PROJECT.ALL_COLUMNS, ORGANIZATION.NAME.as("organization_name"))
                .from(PROJECT).join(ORGANIZATION).on(PROJECT.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                .where(PROJECT.ORGANIZATION_ID.eq(request.getOrganizationId())
                        .and(PROJECT.NAME.like(request.getKeyword()).or(PROJECT.NUM.like(request.getKeyword()))));
        Page<ProjectDTO> page = mapper.paginateAs(Page.of(request.getCurrent(), request.getPageSize()), queryChain, ProjectDTO.class);
        return buildUserInfo(page);
    }

    @Override
    public Page<ProjectDTO> getProjectPageList(OrganizationProjectRequest request) {
        ProjectRequest projectRequest = new ProjectRequest();
        BeanUtils.copyProperties(request, projectRequest);
        return getProjectPageList(projectRequest);
    }

    @Override
    public List<Project> getUserProject(String organizationId, String userId) {
        boolean exists = QueryChain.of(Organization.class).where(ORGANIZATION.ID.eq(organizationId)).exists();
        if (!exists) {
            throw new MSException(Translator.get("organization_not_exist"));
        }
        long count = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ADMIN.name()))).count();
        if (count > 0) {
            return QueryChain.of(Project.class).where(PROJECT.ORGANIZATION_ID.eq(organizationId)).list();
        }
        return QueryChain.of(Project.class).select(QueryMethods.distinct(PROJECT.ALL_COLUMNS))
                .from(PROJECT)
                .join(UserRoleRelation.class).on(USER_ROLE_RELATION.ROLE_ID.eq(USER_ROLE.ID))
                .join(UserRole.class).on(PROJECT.ID.eq(USER_ROLE_RELATION.SOURCE_ID))
                .join(User.class).on(USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER_ROLE_RELATION.USER_ID.eq(userId)
                        .and(USER_ROLE.TYPE.eq("PROJECT"))
                        .and(PROJECT.ORGANIZATION_ID.eq(organizationId))
                        .and(PROJECT.ENABLE.eq(1))).list();
    }

    @Override
    public UserDTO switchProject(ProjectSwitchRequest request, String currentUserId) {
        if (!StringUtils.equals(currentUserId, request.getUserId())) {
            throw new MSException(Translator.get("not_authorized"));
        }
        if (Objects.isNull(mapper.selectOneById(request.getProjectId()))) {
            throw new MSException(Translator.get("project_not_exist"));
        }
        User user = new User();
        user.setId(request.getUserId());
        user.setLastProjectId(request.getProjectId());
        userService.updateById(user);
        //UserDTO userDTO = userService.getUserDtoByKeyword(user.getId());
        return userService.getUserDtoByKeyword(user.getId());
    }

    @Override
    public Project checkResourceExist(String id) {
        return ServiceUtils.checkResourceExist(mapper.selectOneById(id), "permission.project.name");
    }

    @Override
    public void addProjectMember(ProjectAddMemberBatchRequest request, String createUser) {
        addProjectMember(request, createUser, ADD_MEMBER, OperationLogType.ADD.name(), Translator.get("add"), OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }

    @Override
    public List<UserExtendDTO> getUserMemberList(String organizationId, String projectId, String keyword) {
        checkOrgIsExist(organizationId);
        checkProjectNotExist(projectId);
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)).list();
        List<String> userIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).distinct().toList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            return QueryChain.of(User.class).select(QueryMethods.distinct(USER.ID), USER.NAME, USER.EMAIL)
                    .select("count(temp.id) > 0 as memberFlag")
                    .from(USER).as("u").leftJoin(
                            QueryChain.of(UserRoleRelation.class).select(USER_ROLE_RELATION.ALL_COLUMNS)
                                    .where(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)).as("temp")
                    ).on("temp.user_id = u.id")
                    .where(USER.ID.in(userIds)
                            .and(USER.NAME.like(keyword).or(USER.EMAIL.like(keyword))))
                    .groupBy(USER.ID)
                    .orderBy(USER.CREATE_TIME.desc()).limit(100).listAs(UserExtendDTO.class);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserExtendDTO> getUserAdminList(String organizationId, String keyword) {
        checkOrgIsExist(organizationId);
        return QueryChain.of(User.class).select(QueryMethods.distinct(USER.ID, USER.NAME, USER.EMAIL))
                .from(User.class).leftJoin(UserRoleRelation.class).on(USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)
                        .and(USER.EMAIL.like(keyword).or(USER.NAME.like(keyword))))
                .orderBy(USER.CREATE_TIME.desc()).limit(100)
                .listAs(UserExtendDTO.class);
    }

    @Override
    public ProjectDTO update(UpdateProjectRequest request, String updateUser) {
        return update(request, updateUser, UPDATE_PROJECT, OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }

    @Override
    public ProjectDTO update(UpdateProjectRequest updateProjectDto, String updateUser, String path, String module) {
        Project project = new Project();
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(updateProjectDto, project);
        project.setUpdateUser(updateUser);
        checkProjectExistByName(project);
        checkProjectNotExist(project.getId());
        projectDTO.setOrganizationName(organizationMapper.selectOneById(project.getOrganizationId()).getName());
        BeanUtils.copyProperties(project, projectDTO);
        List<String> orgUserIds = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue())))
                .list().stream().map(UserRoleRelation::getUserId).toList();
        List<LogDTO> logDTOList = new ArrayList<>();
        List<String> deleteIds = orgUserIds.stream()
                .filter(item -> !updateProjectDto.getUserIds().contains(item))
                .toList();
        List<String> insertIds = updateProjectDto.getUserIds().stream()
                .filter(item -> !orgUserIds.contains(item))
                .toList();
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            QueryChain<UserRoleRelation> deleteQueryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                    .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))
                    .and(USER_ROLE_RELATION.USER_ID.in(deleteIds)));
            deleteQueryChain.list().forEach(item -> {
                User user = userService.getById(item.getUserId());
                String logProjectId = OperationLogConstants.SYSTEM;
                if (StringUtils.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                    logProjectId = OperationLogConstants.ORGANIZATION;
                }
                LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), item.getId(), updateUser, OperationLogType.DELETE.name(), module, Translator.get("delete") + Translator.get("project_admin") + ": " + user.getName());
                setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
            });
            userRoleRelationMapper.deleteByQuery(deleteQueryChain);
        }
        if (CollectionUtils.isNotEmpty(insertIds)) {
            ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
            memberRequest.setProjectIds(List.of(project.getId()));
            memberRequest.setUserIds(insertIds);
            addProjectAdmin(memberRequest, updateUser, path, OperationLogType.ADD.name(), Translator.get("add"), module);
        }
        if (CollectionUtils.isNotEmpty(logDTOList)) {
            operationLogService.batchAdd(logDTOList);
        }
        //判断是否有模块设置
        if (CollectionUtils.isNotEmpty(updateProjectDto.getModuleIds())) {
            project.setModuleSetting(updateProjectDto.getModuleIds());
            projectDTO.setModuleIds(updateProjectDto.getModuleIds());
        } else {
            project.setModuleSetting(null);
            projectDTO.setModuleIds(new ArrayList<>());
        }
        project.setOrganizationId(null);
        mapper.update(project);
        return projectDTO;
    }

    @Override
    public void enable(String id, String updateUser) {
        checkProjectNotExist(id);
        Project project = new Project();
        project.setId(id);
        project.setEnable(true);
        project.setUpdateUser(updateUser);
        mapper.update(project);
    }

    @Override
    public void disable(String id, String updateUser) {
        checkProjectNotExist(id);
        Project project = new Project();
        project.setId(id);
        project.setEnable(false);
        project.setUpdateUser(updateUser);
        mapper.update(project);
    }

    private void checkOrgIsExist(String organizationId) {
        if (Objects.isNull(organizationMapper.selectOneById(organizationId))) {
            throw new MSException(Translator.get("organization_not_exists"));
        }
    }

    private void addProjectMember(ProjectAddMemberBatchRequest request, String createUser, String path, String type, String content, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        //List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getProjectIds().forEach(projectId -> {
            Project project = getById(projectId);
            Map<String, String> userMap = addUserPre(request, createUser, path, module, projectId, project);
            request.getUserIds().forEach(userId -> {
                List<UserRoleRelation> userRoleRelations1 = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).eq(userId)
                        .and(UserRoleRelation::getSourceId).eq(projectId).list();
                if (userRoleRelations1.isEmpty()) {
                    UserRoleRelation userRoleRelation = UserRoleRelation.builder()
                            .userId(userId)
                            .roleId(InternalUserRole.PROJECT_MEMBER.getValue())
                            .sourceId(projectId)
                            .createUser(createUser)
                            .organizationId(project.getOrganizationId())
                            .build();
                    userRoleRelationMapper.insert(userRoleRelation);
                    String logProjectId = OperationLogConstants.SYSTEM;
                    if (StringUtils.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                        logProjectId = OperationLogConstants.ORGANIZATION;
                    }
                    LogDTO logDTO = new LogDTO(logProjectId, OperationLogConstants.SYSTEM, userRoleRelation.getId(), createUser, type, module, content + Translator.get("project_member") + ": " + userMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
        });
        //if (CollectionUtils.isNotEmpty(userRoleRelations)) {
        //    userRoleRelationMapper.insertBatch(userRoleRelations);
        //}
        operationLogService.batchAdd(logDTOList);
    }

    private Page<ProjectDTO> buildUserInfo(Page<ProjectDTO> page) {
        //取项目的创建人  修改人  删除人到一个list中
        List<String> userIds = new ArrayList<>();
        List<ProjectDTO> projectList = page.getRecords();
        if (CollectionUtils.isNotEmpty(projectList)) {
            userIds.addAll(projectList.stream().map(ProjectDTO::getCreateUser).toList());
            userIds.addAll(projectList.stream().map(ProjectDTO::getUpdateUser).toList());
            userIds.addAll(projectList.stream().map(ProjectDTO::getDeleteUser).toList());
            Map<String, String> userMap = userService.getUserNameMap(userIds.stream().filter(StringUtils::isNotBlank).distinct().toList());
            // 获取项目id
            List<String> projectIds = projectList.stream().map(ProjectDTO::getId).toList();
            List<UserExtendDTO> users = getProjectAdminList(projectIds);
            List<ProjectDTO> projectDTOList = getProjectExtendDTOList(projectIds);
            Map<String, ProjectDTO> projectMap = projectDTOList.stream().collect(Collectors.toMap(ProjectDTO::getId, projectDTO -> projectDTO));
            //根据sourceId分组
            Map<String, List<UserExtendDTO>> userMapList = users.stream().collect(Collectors.groupingBy(UserExtendDTO::getSourceId));
            projectList.forEach(project -> {
                if (CollectionUtils.isNotEmpty(project.getModuleSetting())) {
                    project.setModuleIds(project.getModuleSetting());
                }
                project.setMemberCount(projectMap.get(project.getId()).getMemberCount());
                List<UserExtendDTO> userExtendDTOS = userMapList.get(project.getId());
                if (CollectionUtils.isNotEmpty(userExtendDTOS)) {
                    project.setAdminList(userExtendDTOS);
                    List<String> userIdList = userExtendDTOS.stream().map(User::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(userIdList) && userIdList.contains(project.getCreateUser())) {
                        project.setProjectCreateUserIsAdmin(true);
                    } else {
                        project.setProjectCreateUserIsAdmin(false);
                    }
                } else {
                    project.setAdminList(new ArrayList<>());
                }
                project.setCreateUser(userMap.get(project.getCreateUser()));
                project.setUpdateUser(userMap.get(project.getUpdateUser()));
                project.setDeleteUser(userMap.get(project.getDeleteUser()));
                if (BooleanUtils.isTrue(project.getDeleted())) {
                    project.setRemainDayCount(getDeleteRemainDays(project.getDeleteTime()));
                }
            });
        }
        return page;
    }

    private Integer getDeleteRemainDays(LocalDateTime deleteTime) {
        long remainDays = Duration.between(LocalDateTime.now(), deleteTime).toDays();
        int remainDayCount = DEFAULT_REMAIN_DAY_COUNT - (int) remainDays;
        return remainDayCount > 0 ? remainDayCount : 1;
    }

    private List<UserExtendDTO> getProjectAdminList(List<String> projectIds) {
        return QueryChain.of(UserRoleRelation.class).select(USER_ROLE_RELATION.SOURCE_ID, USER.ALL_COLUMNS)
                .from(USER_ROLE_RELATION).join(USER).on(USER_ROLE_RELATION.USER_ID.eq(USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq("project_admin"))).listAs(UserExtendDTO.class);
    }

    private List<ProjectDTO> getProjectExtendDTOList(List<String> projectIds) {
        return QueryChain.of(UserRoleRelation.class).select(USER_ROLE_RELATION.SOURCE_ID.as("id"),
                        QueryMethods.count(QueryMethods.distinct(USER.ID)).as("memberCount"))
                .from(USER_ROLE_RELATION.as("ur"))
                .leftJoin(USER).as("u").on(USER_ROLE_RELATION.USER_ID.eq(USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds))
                .groupBy(USER_ROLE_RELATION.SOURCE_ID)
                .listAs(ProjectDTO.class);
    }
}
