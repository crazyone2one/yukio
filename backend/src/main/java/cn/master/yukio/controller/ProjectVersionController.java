package cn.master.yukio.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.yukio.entity.ProjectVersion;
import cn.master.yukio.service.IProjectVersionService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 版本管理 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/projectVersion")
public class ProjectVersionController {

    @Autowired
    private IProjectVersionService iProjectVersionService;

    /**
     * 添加版本管理。
     *
     * @param projectVersion 版本管理
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ProjectVersion projectVersion) {
        return iProjectVersionService.save(projectVersion);
    }

    /**
     * 根据主键删除版本管理。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iProjectVersionService.removeById(id);
    }

    /**
     * 根据主键更新版本管理。
     *
     * @param projectVersion 版本管理
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ProjectVersion projectVersion) {
        return iProjectVersionService.updateById(projectVersion);
    }

    /**
     * 查询所有版本管理。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ProjectVersion> list() {
        return iProjectVersionService.list();
    }

    /**
     * 根据版本管理主键获取详细信息。
     *
     * @param id 版本管理主键
     * @return 版本管理详情
     */
    @GetMapping("getInfo/{id}")
    public ProjectVersion getInfo(@PathVariable Serializable id) {
        return iProjectVersionService.getById(id);
    }

    /**
     * 分页查询版本管理。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ProjectVersion> page(Page<ProjectVersion> page) {
        return iProjectVersionService.page(page);
    }

}
