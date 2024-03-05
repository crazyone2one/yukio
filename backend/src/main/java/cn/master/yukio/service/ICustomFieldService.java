package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.CustomField;

/**
 * 自定义字段 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICustomFieldService extends IService<CustomField> {
    boolean isOrganizationTemplateEnable(String orgId, String scene);

    CustomField getWithCheck(String id);
}
