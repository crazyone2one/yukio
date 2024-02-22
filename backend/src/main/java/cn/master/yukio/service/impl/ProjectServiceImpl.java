package cn.master.yukio.service.impl;

import cn.master.yukio.constants.OperationLogModule;
import cn.master.yukio.dto.project.AddProjectRequest;
import cn.master.yukio.dto.project.ProjectDTO;
import cn.master.yukio.dto.project.ProjectRequest;
import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.mapper.ProjectMapper;
import cn.master.yukio.service.IProjectService;
import cn.master.yukio.service.IUserService;
import cn.master.yukio.util.JsonUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.yukio.entity.table.ProjectTableDef.PROJECT;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.yukio.entity.table.UserTableDef.USER;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    private final IUserService userService;

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
        BeanUtils.copyProperties(project, addProjectDTO);
        return projectDTO;
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
                if (StringUtils.isNotBlank(project.getModuleSetting())) {
                    project.setModuleIds(JsonUtils.parseArray(project.getModuleSetting(), String.class));
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
        return QueryChain.of(Project.class).select(PROJECT.ID)
                .from(PROJECT)
                .leftJoin(
                        QueryChain.of(UserRoleRelation.class).select(USER_ROLE_RELATION.SOURCE_ID)
                                .from(USER_ROLE_RELATION)
                                .leftJoin(USER).on(USER_ROLE_RELATION.USER_ID.eq(USER.ID))
                                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds))
                ).as("temp").on(PROJECT.ID.eq("temp.source_id"))
                .groupBy(PROJECT.ID)
                .listAs(ProjectDTO.class);
    }
}
