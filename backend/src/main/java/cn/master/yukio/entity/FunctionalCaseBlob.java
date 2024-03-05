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
 * 功能用例 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "functional_case_blob")

public class FunctionalCaseBlob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 功能用例ID
     */
    private String caseId;

    /**
     * 用例步骤（JSON)，step_model 为 Step 时启用
     */
    private byte[] steps;

    /**
     * 文本描述，step_model 为 Text 时启用
     */
    private byte[] textDescription;

    /**
     * 预期结果，step_model 为 Text  时启用
     */
    private byte[] expectedResult;

    /**
     * 前置条件
     */
    private byte[] prerequisite;

    /**
     * 备注
     */
    private byte[] description;

}
