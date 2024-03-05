package cn.master.yukio.service.impl;

import cn.master.yukio.service.IOrganizationParameterService;
import cn.master.yukio.util.ServiceUtils;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.CustomField;
import cn.master.yukio.mapper.CustomFieldMapper;
import cn.master.yukio.service.ICustomFieldService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 自定义字段 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CustomFieldServiceImpl extends ServiceImpl<CustomFieldMapper, CustomField> implements ICustomFieldService {
    private final IOrganizationParameterService organizationParameterService;

    @Override
    public boolean isOrganizationTemplateEnable(String orgId, String scene) {
        String key = organizationParameterService.getOrgTemplateEnableKeyByScene(scene);
        String value = organizationParameterService.getValue(orgId, key);
        return !StringUtils.equals(BooleanUtils.toStringTrueFalse(false), value);
    }

    @Override
    public CustomField getWithCheck(String id) {
        checkResourceExist(id);
        return getById(id);
    }

    private CustomField checkResourceExist(String id) {
        return ServiceUtils.checkResourceExist(getById(id), "permission.organization_custom_field.name");
    }
}
