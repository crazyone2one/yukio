package cn.master.yukio.service;

import cn.master.yukio.dto.project.TemplateCustomFieldRequest;
import cn.master.yukio.dto.project.TemplateSystemCustomFieldRequest;
import cn.master.yukio.dto.project.TemplateUpdateRequest;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.Template;

import java.util.List;

/**
 * 模版 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITemplateService extends IService<Template> {

    Template add(TemplateUpdateRequest request, String userId);

    Template baseAdd(Template template, List<TemplateCustomFieldRequest> customFields, List<TemplateSystemCustomFieldRequest> systemFields);

    Template update(TemplateUpdateRequest request);

    Template update(Template template, List<TemplateCustomFieldRequest> customFields, List<TemplateSystemCustomFieldRequest> systemFields);

    Template getWithCheck(String id);

    void delete(String id);

    String getDefaultTemplateId(String projectId, String scene);

    void setDefaultTemplate(String projectId, String id);
}
