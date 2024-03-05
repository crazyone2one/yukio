package cn.master.yukio.resolver.field;

import cn.master.yukio.dto.system.CustomFieldDao;
import cn.master.yukio.entity.CustomFieldOption;
import cn.master.yukio.util.CommonBeanFactory;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public class CustomFieldSelectResolver extends AbstractCustomFieldResolver{
    @Override
    public void validate(CustomFieldDao customField, Object value) {
        validateRequired(customField, value);
        if (value == null) {
            return;
        }
        validateString(customField.getName(), value);
        if (StringUtils.isBlank((String) value)) {
            return;
        }
        List<CustomFieldOption> options = getOptions(customField.getId());
        Set<String> values = options.stream().map(CustomFieldOption::getValue).collect(Collectors.toSet());
        if (!values.contains(value)) {
            throwValidateException(customField.getName());
        }
    }
    protected List<CustomFieldOption> getOptions(String id) {
        return QueryChain.of(CustomFieldOption.class).where(CustomFieldOption::getFieldId).eq(id).list();
    }
}
