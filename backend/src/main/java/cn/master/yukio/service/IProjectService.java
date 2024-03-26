package cn.master.yukio.service;

import cn.master.yukio.dto.project.*;
import cn.master.yukio.dto.request.ProjectAddMemberBatchRequest;
import cn.master.yukio.dto.user.UserDTO;
import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.Project;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IProjectService extends IService<Project> {
    ProjectDTO add(AddProjectRequest addProjectDTO, String createUser, String path, String module);

    ProjectDTO add(AddProjectRequest request, String createUser);

    Page<ProjectDTO> getProjectPageList(ProjectRequest request);

    Page<ProjectDTO> getProjectPageList(OrganizationProjectRequest request);

    List<Project> getUserProject(String organizationId, String userId);

    UserDTO switchProject(ProjectSwitchRequest request, String currentUserId);

    Project checkResourceExist(String id);

    void addProjectMember(ProjectAddMemberBatchRequest request, String createUser);

    List<UserExtendDTO> getUserMemberList(String organizationId, String projectId, String keyword);

    List<UserExtendDTO> getUserAdminList(String organizationId, String keyword);

    ProjectDTO update(UpdateProjectRequest request, String updateUser);

    ProjectDTO update(UpdateProjectRequest updateProjectDto, String updateUser, String path, String module);

    void enable(String id, String updateUser);

    void disable(String id, String updateUser);

    int remove(String id, String deleteUserId);

    int revoke(String id, String userId);
}
