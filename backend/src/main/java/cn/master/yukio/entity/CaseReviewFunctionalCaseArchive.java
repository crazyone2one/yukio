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
 * 用例评审归档表 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "case_review_functional_case_archive")

public class CaseReviewFunctionalCaseArchive implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用例评审ID
     */
    private String reviewId;

    /**
     * 功能用例ID
     */
    private String caseId;

    /**
     * 功能用例快照（JSON)
     */
    private byte[] content;

    @Id
    private String id;

}
