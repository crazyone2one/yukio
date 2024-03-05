package cn.master.yukio.controller;

import cn.master.yukio.dto.project.TemplateUpdateRequest;
import cn.master.yukio.entity.Template;
import cn.master.yukio.service.ITemplateService;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 模版 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/project/template")
public class TemplateController {
    private final ITemplateService iTemplateService;

    /**
     * 添加模版。
     *
     * @param request 模版
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public Template save(@Validated({Created.class}) @RequestBody TemplateUpdateRequest request) {
        return iTemplateService.add(request, SessionUtils.getUserId());
    }

    /**
     * 根据主键删除模版。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @GetMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        iTemplateService.delete(id);
    }

    /**
     * 根据主键更新模版。
     *
     * @param request 模版
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public Template update(@Validated({Updated.class}) @RequestBody TemplateUpdateRequest request) {
        return iTemplateService.update(request);
    }

    /**
     * 查询所有模版。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Template> list() {
        return iTemplateService.list();
    }

    /**
     * 根据模版主键获取详细信息。
     *
     * @param id 模版主键
     * @return 模版详情
     */
    @GetMapping("getInfo/{id}")
    public Template getInfo(@PathVariable Serializable id) {
        return iTemplateService.getById(id);
    }

    /**
     * 分页查询模版。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Template> page(Page<Template> page) {
        return iTemplateService.page(page);
    }

    /**
     * 设置默认模板
     *
     * @param projectId
     * @param id
     */
    @GetMapping("/set-default/{projectId}/{id}")
    public void setDefaultTemplate(@PathVariable String projectId, @PathVariable String id) {
        iTemplateService.setDefaultTemplate(projectId, id);
    }

}
