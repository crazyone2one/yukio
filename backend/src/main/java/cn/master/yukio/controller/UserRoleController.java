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
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.service.IUserRoleService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用户组 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    @Autowired
    private IUserRoleService iUserRoleService;

    /**
     * 添加用户组。
     *
     * @param userRole 用户组
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody UserRole userRole) {
        return iUserRoleService.save(userRole);
    }

    /**
     * 根据主键删除用户组。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iUserRoleService.removeById(id);
    }

    /**
     * 根据主键更新用户组。
     *
     * @param userRole 用户组
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody UserRole userRole) {
        return iUserRoleService.updateById(userRole);
    }

    /**
     * 查询所有用户组。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<UserRole> list() {
        return iUserRoleService.list();
    }

    /**
     * 根据用户组主键获取详细信息。
     *
     * @param id 用户组主键
     * @return 用户组详情
     */
    @GetMapping("getInfo/{id}")
    public UserRole getInfo(@PathVariable Serializable id) {
        return iUserRoleService.getById(id);
    }

    /**
     * 分页查询用户组。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<UserRole> page(Page<UserRole> page) {
        return iUserRoleService.page(page);
    }

}
