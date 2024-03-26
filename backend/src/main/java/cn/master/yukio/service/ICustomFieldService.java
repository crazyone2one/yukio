package cn.master.yukio.service;

import cn.master.yukio.dto.BasePageRequest;
import cn.master.yukio.dto.CustomFieldDTO;
import cn.master.yukio.entity.CustomFieldOption;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.CustomField;

import java.util.List;

/**
 * 自定义字段 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICustomFieldService extends IService<CustomField> {
    boolean isOrganizationTemplateEnable(String orgId, String scene);

    CustomField getWithCheck(String id);

    List<CustomField> getByIds(List<String> fieldIds);

    Page<CustomFieldDTO> page(String organizationId, String scene, BasePageRequest page);

    List<CustomField> getByScopeIdAndScene(String scopeId, String scene);

}
