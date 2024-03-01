package cn.master.yukio.dto.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@Data
public class TestPlanResponse {
    @Schema(description = "测试计划ID")
    private String id;
    @Schema(description = "项目ID")
    private String projectId;
    @Schema(description = "测试计划编号")
    private long num;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "测试计划类型 测试计划/测试计划组")
    private String type;
    @Schema(description = "标签")
    private List<String> tags;
    @Schema(description = "定时任务")
    private String schedule;
    @Schema(description = "创建人")
    private String createUser;
    @Schema(description = "创建时间")
    private String createTime;
    @Schema(description = "模块")
    private String moduleName;
    @Schema(description = "模块Id")
    private String moduleId;
    @Schema(description = "测试计划组内的测试计划")
    List<TestPlanResponse> children;

    @Schema(description = "测试计划组Id")
    private String testPlanGroupId;
}

