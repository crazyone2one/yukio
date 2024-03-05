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
 * 评审和评审人中间表 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "case_review_user")

public class CaseReviewUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评审ID
     */
    @Id
    private String reviewId;

    /**
     * 评审人ID
     */
    @Id
    private String userId;

    @Id
    private String id;

}
