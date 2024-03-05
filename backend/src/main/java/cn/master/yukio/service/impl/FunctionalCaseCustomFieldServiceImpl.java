package cn.master.yukio.service.impl;

import cn.master.yukio.dto.functional.CaseCustomFieldDTO;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.FunctionalCaseCustomField;
import cn.master.yukio.mapper.FunctionalCaseCustomFieldMapper;
import cn.master.yukio.service.IFunctionalCaseCustomFieldService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
