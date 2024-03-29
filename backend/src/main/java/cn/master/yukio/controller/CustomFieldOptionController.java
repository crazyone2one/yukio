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
import cn.master.yukio.entity.CustomFieldOption;
import cn.master.yukio.service.ICustomFieldOptionService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 自定义字段选项 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/customFieldOption")
public class CustomFieldOptionController {

    @Autowired
    private ICustomFieldOptionService iCustomFieldOptionService;

    /**
     * 添加自定义字段选项。
     *
     * @param customFieldOption 自定义字段选项
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CustomFieldOption customFieldOption) {
        return iCustomFieldOptionService.save(customFieldOption);
    }

    /**
     * 根据主键删除自定义字段选项。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCustomFieldOptionService.removeById(id);
    }

    /**
     * 根据主键更新自定义字段选项。
     *
     * @param customFieldOption 自定义字段选项
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CustomFieldOption customFieldOption) {
        return iCustomFieldOptionService.updateById(customFieldOption);
    }

    /**
     * 查询所有自定义字段选项。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CustomFieldOption> list() {
        return iCustomFieldOptionService.list();
    }

    /**
     * 根据自定义字段选项主键获取详细信息。
     *
     * @param id 自定义字段选项主键
     * @return 自定义字段选项详情
     */
    @GetMapping("getInfo/{id}")
    public CustomFieldOption getInfo(@PathVariable Serializable id) {
        return iCustomFieldOptionService.getById(id);
    }

    /**
     * 分页查询自定义字段选项。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CustomFieldOption> page(Page<CustomFieldOption> page) {
        return iCustomFieldOptionService.page(page);
    }

}
