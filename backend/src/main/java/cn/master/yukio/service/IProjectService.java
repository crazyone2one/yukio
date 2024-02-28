package cn.master.yukio.service;

import cn.master.yukio.dto.project.AddProjectRequest;
import cn.master.yukio.dto.project.OrganizationProjectRequest;
import cn.master.yukio.dto.project.ProjectDTO;
import cn.master.yukio.dto.project.ProjectRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.Project;

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
}
