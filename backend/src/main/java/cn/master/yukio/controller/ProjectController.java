package cn.master.yukio.controller;

import cn.master.yukio.dto.project.*;
import cn.master.yukio.dto.request.ProjectAddMemberBatchRequest;
import cn.master.yukio.dto.request.ProjectAddMemberRequest;
import cn.master.yukio.dto.user.UserDTO;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.User;
import cn.master.yukio.service.IProjectService;
import cn.master.yukio.service.IUserService;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 系统设置-系统-组织与项目-项目
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/system/project")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService iProjectService;
    private final IUserService userService;

    /**
     * 添加项目。
     *
     * @param request 项目
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public ProjectDTO save(@RequestBody @Validated({Created.class}) AddProjectRequest request) {
        return iProjectService.add(request, SessionUtils.getUserId());
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public int remove(@PathVariable String id) {
        return iProjectService.remove(id, SessionUtils.getUserId());
    }

    /**
     * 系统设置-系统-组织与项目-项目-编辑
     *
     * @param request 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public ProjectDTO update(@RequestBody @Validated({Updated.class}) UpdateProjectRequest request) {
        return iProjectService.update(request, SessionUtils.getUserId());
    }

    /**
     * 查询所有项目。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Project> list() {
        return iProjectService.list();
    }

    /**
     * 根据项目主键获取详细信息。
     *
     * @param id 项目主键
     * @return 项目详情
     */
    @GetMapping("getInfo/{id}")
    public Project getInfo(@PathVariable Serializable id) {
        return iProjectService.getById(id);
    }

    /**
     * 系统设置-系统-组织与项目-项目-获取项目列表。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<ProjectDTO> page(@Validated @RequestBody ProjectRequest request) {
        return iProjectService.getProjectPageList(request);
    }

    /**
     * 系统设置-系统-组织与项目-项目-系统-组织及项目, 获取管理员下拉选项
     *
     * @param keyword
     * @return java.util.List<cn.master.yukio.entity.User>
     */
    @GetMapping("/user-list")
    public List<User> getUserList(@RequestParam(value = "keyword", required = false) String keyword) {
        return userService.getUserList(keyword);
    }

    /**
     * 根据组织ID获取所有有权限的项目
     *
     * @param organizationId
     * @return java.util.List<cn.master.yukio.entity.Project>
     */
    @GetMapping("/list/options/{organizationId}")
    public List<Project> getUserProject(@PathVariable String organizationId) {
        return iProjectService.getUserProject(organizationId, SessionUtils.getUserId());
    }

    @PostMapping("/switch")
    public UserDTO switchProject(@RequestBody ProjectSwitchRequest request) {
        return iProjectService.switchProject(request, SessionUtils.getUserId());
    }

    /**
     * 系统设置-系统-组织与项目-项目-添加成员
     *
     * @param request
     */
    @PostMapping("/add-member")
    public void addProjectMember(@Validated @RequestBody ProjectAddMemberRequest request) {
        ProjectAddMemberBatchRequest batchRequest = new ProjectAddMemberBatchRequest();
        batchRequest.setProjectIds(List.of(request.getProjectId()));
        batchRequest.setUserIds(request.getUserIds());
        iProjectService.addProjectMember(batchRequest, SessionUtils.getUserId());
    }

    @GetMapping("/enable/{id}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-启用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    public void enable(@PathVariable String id) {
        iProjectService.enable(id, SessionUtils.getUserId());
    }

    @GetMapping("/disable/{id}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-禁用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    public void disable(@PathVariable String id) {
        iProjectService.disable(id, SessionUtils.getUserId());
    }

    @GetMapping("/revoke/{id}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-撤销删除")
    public int revokeProject(@PathVariable String id) {
        return iProjectService.revoke(id, SessionUtils.getUserId());
    }
}
