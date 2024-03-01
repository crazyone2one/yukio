package cn.master.yukio.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.mybatisflex.core.handler.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.LinkedHashSet;

/**
 * 测试计划 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "test_plan")

public class TestPlan implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * num
     */
    private Long num;

    /**
     * 测试计划所属项目
     */
    private String projectId;

    /**
     * 测试计划组ID;默认为none.只关联type为group的测试计划
     */
    private String groupId;

    /**
     * 测试计划模块ID
     */
    private String moduleId;

    /**
     * 测试计划名称
     */
    private String name;

    /**
     * 测试计划状态;未开始，进行中，已完成，已归档
     */
    private String status;

    /**
     * 数据类型;测试计划组（group）/测试计划（testPlan）
     */
    private String type;

    /**
     * 标签
     */
    @Column(typeHandler = JacksonTypeHandler.class)
    private LinkedHashSet<String> tags;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 计划开始时间
     */
    private LocalDateTime plannedStartTime;

    /**
     * 计划结束时间
     */
    private LocalDateTime plannedEndTime;

    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 描述
     */
    private String description;

}
