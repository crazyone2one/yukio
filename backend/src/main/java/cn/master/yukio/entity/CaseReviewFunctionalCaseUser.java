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
 * 功能用例评审和评审人的中间表 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "case_review_functional_case_user")

public class CaseReviewFunctionalCaseUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 功能用例ID
     */
    private String caseId;

    /**
     * 评审ID
     */
    private String reviewId;

    /**
     * 评审人ID
     */
    private String userId;

    @Id
    private String id;

}
