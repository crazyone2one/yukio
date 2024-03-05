package cn.master.yukio.resolver.field;

import cn.master.yukio.dto.system.CustomFieldDao;
import cn.master.yukio.util.JsonUtils;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public class CustomFieldMultipleMemberResolver extends CustomFieldMemberResolver {

    @Override
    public void validate(CustomFieldDao customField, Object value) {
        validateArrayRequired(customField, value);
        validateArray(customField.getName(), value);
    }

    @Override
    public String parse2String(Object value) {
        return JsonUtils.toJsonString(value);
    }

    @Override
    public Object parse2Value(String value) {
        return parse2Array(value);
    }
}

