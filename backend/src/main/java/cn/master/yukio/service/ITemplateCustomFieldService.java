package cn.master.yukio.service;

import cn.master.yukio.dto.project.TemplateCustomFieldRequest;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.TemplateCustomField;

import java.util.List;

/**
 * 模板和字段的关联关系 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITemplateCustomFieldService extends IService<TemplateCustomField> {
    void addCustomFieldByTemplateId(String id, List<TemplateCustomFieldRequest> customFieldRequests);

    void addSystemFieldByTemplateId(String id, List<TemplateCustomFieldRequest> customFieldRequests);

    void deleteByTemplateIdAndSystem(String id, boolean isSystem);

    List<TemplateCustomField> getByTemplateId(String id);
}
