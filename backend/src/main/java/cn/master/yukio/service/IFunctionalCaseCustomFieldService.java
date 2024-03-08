package cn.master.yukio.service;

import cn.master.yukio.dto.functional.CaseCustomFieldDTO;
import cn.master.yukio.dto.functional.FunctionalCaseCustomFieldDTO;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.FunctionalCaseCustomField;

import java.util.List;

/**
 * 自定义字段功能用例关系 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IFunctionalCaseCustomFieldService extends IService<FunctionalCaseCustomField> {

    void saveCustomField(String id, List<CaseCustomFieldDTO> customFields);

    List<FunctionalCaseCustomFieldDTO> getCustomFieldsByCaseIds(List<String> ids);
}
