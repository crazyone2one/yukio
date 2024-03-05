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
import cn.master.yukio.entity.CustomField;
import cn.master.yukio.service.ICustomFieldService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 自定义字段 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/customField")
public class CustomFieldController {

    @Autowired
    private ICustomFieldService iCustomFieldService;

    /**
     * 添加自定义字段。
     *
     * @param customField 自定义字段
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CustomField customField) {
        return iCustomFieldService.save(customField);
    }

    /**
     * 根据主键删除自定义字段。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCustomFieldService.removeById(id);
    }

    /**
     * 根据主键更新自定义字段。
     *
     * @param customField 自定义字段
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CustomField customField) {
        return iCustomFieldService.updateById(customField);
    }

    /**
     * 查询所有自定义字段。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CustomField> list() {
        return iCustomFieldService.list();
    }

    /**
     * 根据自定义字段主键获取详细信息。
     *
     * @param id 自定义字段主键
     * @return 自定义字段详情
     */
    @GetMapping("getInfo/{id}")
    public CustomField getInfo(@PathVariable Serializable id) {
        return iCustomFieldService.getById(id);
    }

    /**
     * 分页查询自定义字段。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CustomField> page(Page<CustomField> page) {
        return iCustomFieldService.page(page);
    }

}
