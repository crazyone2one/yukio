package cn.master.yukio.service.impl;

import cn.master.yukio.dto.functional.CaseCustomFieldDTO;
import cn.master.yukio.dto.functional.FunctionalCaseCustomFieldDTO;
import cn.master.yukio.entity.FunctionalCaseCustomField;
import cn.master.yukio.mapper.FunctionalCaseCustomFieldMapper;
import cn.master.yukio.service.IFunctionalCaseCustomFieldService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.yukio.entity.table.CustomFieldTableDef.CUSTOM_FIELD;
import static cn.master.yukio.entity.table.FunctionalCaseCustomFieldTableDef.FUNCTIONAL_CASE_CUSTOM_FIELD;

/**
 * 自定义字段功能用例关系 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class FunctionalCaseCustomFieldServiceImpl extends ServiceImpl<FunctionalCaseCustomFieldMapper, FunctionalCaseCustomField> implements IFunctionalCaseCustomFieldService {

    @Override
    public void saveCustomField(String caseId, List<CaseCustomFieldDTO> customFields) {
        customFields.forEach(item -> {
            FunctionalCaseCustomField build = FunctionalCaseCustomField.builder().caseId(caseId).fieldId(item.getFieldId()).value(item.getValue()).build();
            mapper.insert(build);
        });
    }

    @Override
    public List<FunctionalCaseCustomFieldDTO> getCustomFieldsByCaseIds(List<String> ids) {
        return queryChain().select(FUNCTIONAL_CASE_CUSTOM_FIELD.CASE_ID.as("caseId"),
                        FUNCTIONAL_CASE_CUSTOM_FIELD.FIELD_ID.as("fieldId"),
                        FUNCTIONAL_CASE_CUSTOM_FIELD.VALUE.as("defaultValue"),
                        CUSTOM_FIELD.NAME.as("fieldName"),
                        CUSTOM_FIELD.INTERNAL.as("internal"),
                        CUSTOM_FIELD.TYPE)
                .from(FUNCTIONAL_CASE_CUSTOM_FIELD)
                .innerJoin(CUSTOM_FIELD).on(FUNCTIONAL_CASE_CUSTOM_FIELD.FIELD_ID.eq(CUSTOM_FIELD.ID))
                .where(FUNCTIONAL_CASE_CUSTOM_FIELD.CASE_ID.in(ids)).listAs(FunctionalCaseCustomFieldDTO.class);
    }
}
