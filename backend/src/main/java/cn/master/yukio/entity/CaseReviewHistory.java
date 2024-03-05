package cn.master.yukio.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 评审历史表 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "case_review_history")

public class CaseReviewHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 评审ID
     */
    private String reviewId;

    /**
     * 用例ID
     */
    private String caseId;

    /**
     * 评审意见
     */
    private byte[] content;

    /**
     * 评审结果：通过/不通过/建议
     */
    private String status;

    /**
     * 是否是取消关联或评审被删除的：0-否，1-是
     */
    private Boolean deleted;

    /**
     * 是否是废弃的评审记录：0-否，1-是
     */
    private Boolean abandoned;

    /**
     * 通知人
     */
    private String notifier;

    /**
     * 操作人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
