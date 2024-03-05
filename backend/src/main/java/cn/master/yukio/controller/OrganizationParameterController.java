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
import cn.master.yukio.entity.OrganizationParameter;
import cn.master.yukio.service.IOrganizationParameterService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 组织参数 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/organizationParameter")
public class OrganizationParameterController {

    @Autowired
    private IOrganizationParameterService iOrganizationParameterService;

    /**
     * 添加组织参数。
     *
     * @param organizationParameter 组织参数
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody OrganizationParameter organizationParameter) {
        return iOrganizationParameterService.save(organizationParameter);
    }

    /**
     * 根据主键删除组织参数。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iOrganizationParameterService.removeById(id);
    }

    /**
     * 根据主键更新组织参数。
     *
     * @param organizationParameter 组织参数
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody OrganizationParameter organizationParameter) {
        return iOrganizationParameterService.updateById(organizationParameter);
    }

    /**
     * 查询所有组织参数。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<OrganizationParameter> list() {
        return iOrganizationParameterService.list();
    }

    /**
     * 根据组织参数主键获取详细信息。
     *
     * @param id 组织参数主键
     * @return 组织参数详情
     */
    @GetMapping("getInfo/{id}")
    public OrganizationParameter getInfo(@PathVariable Serializable id) {
        return iOrganizationParameterService.getById(id);
    }

    /**
     * 分页查询组织参数。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<OrganizationParameter> page(Page<OrganizationParameter> page) {
        return iOrganizationParameterService.page(page);
    }

}
