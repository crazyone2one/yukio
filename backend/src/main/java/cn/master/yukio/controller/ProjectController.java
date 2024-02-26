package cn.master.yukio.controller;

import cn.master.yukio.dto.project.AddProjectRequest;
import cn.master.yukio.dto.project.ProjectDTO;
import cn.master.yukio.dto.project.ProjectRequest;
import cn.master.yukio.entity.User;
import cn.master.yukio.service.IUserService;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.master.yukio.entity.Project;
import cn.master.yukio.service.IProjectService;

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
    public boolean remove(@PathVariable Serializable id) {
        return iProjectService.removeById(id);
    }

    /**
     * 根据主键更新项目。
     *
     * @param project 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Project project) {
        return iProjectService.updateById(project);
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
}
