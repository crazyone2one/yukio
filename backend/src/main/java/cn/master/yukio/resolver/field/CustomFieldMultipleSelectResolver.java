package cn.master.yukio.resolver.field;

import cn.master.yukio.dto.system.CustomFieldDao;
import cn.master.yukio.entity.CustomFieldOption;
import cn.master.yukio.util.JsonUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public class CustomFieldMultipleSelectResolver extends CustomFieldSelectResolver{

    @Override
    public Object parse2Value(String value) {
        return super.parse2Value(value);
    }

    @Override
    public String parse2String(Object value) {
        return JsonUtils.toJsonString(value);
    }

    @Override
    public void validate(CustomFieldDao customField, Object value) {
        validateArrayRequired(customField, value);
        validateArray(customField.getName(), value);
        List<CustomFieldOption> options = getOptions(customField.getId());
        Set<String> values = options.stream().map(CustomFieldOption::getValue).collect(Collectors.toSet());
        for (String item : (List<String>)value) {
            if (!values.contains(item)) {
                throwValidateException(customField.getName());
            }
        }
    }
}
