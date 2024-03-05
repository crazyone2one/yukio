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
 * 自定义字段功能用例关系 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "functional_case_custom_field")

public class FunctionalCaseCustomField implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 资源ID
     */
    @Id
    private String caseId;

    /**
     * 字段ID
     */
    @Id
    private String fieldId;

    /**
     * 字段值
     */
    private String value;

}
