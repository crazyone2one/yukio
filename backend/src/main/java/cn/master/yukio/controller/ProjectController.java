package cn.master.yukio.controller;

import cn.master.yukio.dto.project.AddProjectRequest;
import cn.master.yukio.dto.project.ProjectDTO;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.master.yukio.entity.Project;
import cn.master.yukio.service.IProjectService;
import org.springframework.web.bind.annotation.RestController;
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
     * 分页查询项目。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Project> page(Page<Project> page) {
        return iProjectService.page(page);
    }

}
