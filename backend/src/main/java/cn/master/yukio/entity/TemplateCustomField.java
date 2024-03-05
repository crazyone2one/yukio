package cn.master.yukio.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 模板和字段的关联关系 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "template_custom_field")

public class TemplateCustomField implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 字段ID
     */
    private String fieldId;

    /**
     * 模版ID
     */
    private String templateId;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否是系统字段
     */
    private Boolean systemField;

    /**
     * 排序字段
     */
    private Integer pos;

    /**
     * api字段名
     */
    private String apiFieldId;

    /**
     * 默认值
     */
    private String defaultValue;

}
