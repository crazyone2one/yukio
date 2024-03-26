package cn.master.yukio.dto;

import cn.master.yukio.entity.CustomField;
import cn.master.yukio.entity.CustomFieldOption;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 03/26/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomFieldDTO extends CustomField {
    private List<CustomFieldOption> options;
    /**
     * 是否被模板使用
     */
    private Boolean used = false;
    /**
     * 模板中该字段是否必选
     */
    private Boolean templateRequired = false;
}
