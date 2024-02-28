package cn.master.yukio.controller;

import cn.master.yukio.dto.project.OrganizationProjectRequest;
import cn.master.yukio.dto.project.ProjectDTO;
import cn.master.yukio.dto.project.ProjectRequest;
import cn.master.yukio.service.IProjectService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统设置-组织-项目
 *
 * @author Created by 11's papa on 02/28/2024
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/organization/project")
public class OrganizationProjectController {
    private final IProjectService projectService;

    /**
     * 系统设置-组织-项目-获取项目列表
     *
     * @param request
     * @return com.mybatisflex.core.paginate.Page<cn.master.yukio.dto.project.ProjectDTO>
     */
    @PostMapping("/page")
    public Page<ProjectDTO> page(@Validated @RequestBody OrganizationProjectRequest request) {
        return projectService.getProjectPageList(request);
    }
}
