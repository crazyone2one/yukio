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
 * 测试计划配置 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "test_plan_config")

public class TestPlanConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 测试计划ID
     */
    @Id
    private String testPlanId;

    /**
     * 运行模式
     */
    private String runModeConfig;

    /**
     * 是否自定更新功能用例状态
     */
    private Boolean automaticStatusUpdate;

    /**
     * 是否允许重复添加用例
     */
    private Boolean repeatCase;

    /**
     * 测试计划通过阈值;0-100
     */
    private Double passThreshold;

}
