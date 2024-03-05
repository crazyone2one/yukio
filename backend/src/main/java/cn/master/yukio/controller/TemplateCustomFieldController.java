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
import cn.master.yukio.entity.TemplateCustomField;
import cn.master.yukio.service.ITemplateCustomFieldService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 模板和字段的关联关系 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/templateCustomField")
public class TemplateCustomFieldController {

    @Autowired
    private ITemplateCustomFieldService iTemplateCustomFieldService;

    /**
     * 添加模板和字段的关联关系。
     *
     * @param templateCustomField 模板和字段的关联关系
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody TemplateCustomField templateCustomField) {
        return iTemplateCustomFieldService.save(templateCustomField);
    }

    /**
     * 根据主键删除模板和字段的关联关系。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iTemplateCustomFieldService.removeById(id);
    }

    /**
     * 根据主键更新模板和字段的关联关系。
     *
     * @param templateCustomField 模板和字段的关联关系
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody TemplateCustomField templateCustomField) {
        return iTemplateCustomFieldService.updateById(templateCustomField);
    }

    /**
     * 查询所有模板和字段的关联关系。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TemplateCustomField> list() {
        return iTemplateCustomFieldService.list();
    }

    /**
     * 根据模板和字段的关联关系主键获取详细信息。
     *
     * @param id 模板和字段的关联关系主键
     * @return 模板和字段的关联关系详情
     */
    @GetMapping("getInfo/{id}")
    public TemplateCustomField getInfo(@PathVariable Serializable id) {
        return iTemplateCustomFieldService.getById(id);
    }

    /**
     * 分页查询模板和字段的关联关系。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TemplateCustomField> page(Page<TemplateCustomField> page) {
        return iTemplateCustomFieldService.page(page);
    }

}
