package cn.master.yukio.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 用例评审 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "case_review")

public class CaseReview implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 业务ID
     */
    private Long num;

    /**
     * 名称
     */
    private String name;

    /**
     * 模块id
     */
    private String moduleId;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 评审状态：未开始/进行中/已完成/已结束/已归档
     */
    private String status;

    /**
     * 通过标准：单人通过/全部通过
     */
    private String reviewPassRule;

    /**
     * 自定义排序，间隔5000
     */
    private Long pos;

    /**
     * 评审开始时间
     */
    private LocalDateTime startTime;

    /**
     * 评审结束时间
     */
    private LocalDateTime endTime;

    /**
     * 用例数
     */
    private Integer caseCount;

    /**
     * 通过率(保留两位小数)
     */
    private BigDecimal passRate;

    /**
     * 标签
     */
    private String tags;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateUser;

}
