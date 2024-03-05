package cn.master.yukio.resolver.field;

import cn.master.yukio.dto.system.CustomFieldDao;
import cn.master.yukio.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public class CustomFieldDateTimeResolver extends AbstractCustomFieldResolver{
    @Override
    public void validate(CustomFieldDao customField, Object value) {
        validateRequired(customField, value);
        try {
            if (value != null && StringUtils.isNotBlank(value.toString())) {
                DateUtils.getTime(value.toString());
            }
        } catch (Exception e) {
            throwValidateException(customField.getName());
        }
    }
}
