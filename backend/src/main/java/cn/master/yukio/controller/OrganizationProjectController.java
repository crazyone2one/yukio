package cn.master.yukio.controller;

import cn.master.yukio.dto.project.OrganizationProjectRequest;
import cn.master.yukio.dto.project.ProjectDTO;
import cn.master.yukio.dto.project.ProjectRequest;
import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.service.IProjectService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user-admin-list/{organizationId}")
    @Operation(summary = "系统设置-组织-项目-获取管理员列表")
    public List<UserExtendDTO> getUserAdminList(@PathVariable String organizationId, @Schema(description = "查询关键字，根据邮箱和用户名查询")
    @RequestParam(value = "keyword", required = false) String keyword) {
        return projectService.getUserAdminList(organizationId, keyword);
    }

    @GetMapping("/user-member-list/{organizationId}/{projectId}")
    @Operation(summary = "系统设置-组织-项目-获取成员列表")
    public List<UserExtendDTO> getUserMemberList(@PathVariable String organizationId, @PathVariable String projectId,
                                                 @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                                 @RequestParam(value = "keyword", required = false) String keyword) {
        return projectService.getUserMemberList(organizationId, projectId, keyword);
    }
}
