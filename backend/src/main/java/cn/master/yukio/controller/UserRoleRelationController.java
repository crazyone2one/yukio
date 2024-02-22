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
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.service.IUserRoleRelationService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用户组关系 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/userRoleRelation")
public class UserRoleRelationController {

    @Autowired
    private IUserRoleRelationService iUserRoleRelationService;

    /**
     * 添加用户组关系。
     *
     * @param userRoleRelation 用户组关系
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody UserRoleRelation userRoleRelation) {
        return iUserRoleRelationService.save(userRoleRelation);
    }

    /**
     * 根据主键删除用户组关系。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iUserRoleRelationService.removeById(id);
    }

    /**
     * 根据主键更新用户组关系。
     *
     * @param userRoleRelation 用户组关系
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody UserRoleRelation userRoleRelation) {
        return iUserRoleRelationService.updateById(userRoleRelation);
    }

    /**
     * 查询所有用户组关系。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<UserRoleRelation> list() {
        return iUserRoleRelationService.list();
    }

    /**
     * 根据用户组关系主键获取详细信息。
     *
     * @param id 用户组关系主键
     * @return 用户组关系详情
     */
    @GetMapping("getInfo/{id}")
    public UserRoleRelation getInfo(@PathVariable Serializable id) {
        return iUserRoleRelationService.getById(id);
    }

    /**
     * 分页查询用户组关系。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<UserRoleRelation> page(Page<UserRoleRelation> page) {
        return iUserRoleRelationService.page(page);
    }

}
