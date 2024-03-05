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
 * 自定义字段选项 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "custom_field_option")

public class CustomFieldOption implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自定义字段ID
     */

    private String fieldId;

    /**
     * 选项值
     */

    private String value;

    /**
     * 选项值名称
     */
    private String text;

    /**
     * 是否内置
     */
    private Boolean internal;

}
