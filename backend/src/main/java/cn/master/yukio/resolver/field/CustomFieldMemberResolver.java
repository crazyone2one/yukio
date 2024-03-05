package cn.master.yukio.resolver.field;

import cn.master.yukio.dto.system.CustomFieldDao;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public class CustomFieldMemberResolver extends AbstractCustomFieldResolver {

    @Override
    public void validate(CustomFieldDao customField, Object value) {
        validateRequired(customField, value);
        validateString(customField.getName(), value);
    }
}
