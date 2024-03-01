package cn.master.yukio.dto.plan;

import cn.master.yukio.dto.TableBatchProcessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TestPlanBatchProcessRequest extends TableBatchProcessDTO {
    @Schema(description = "项目ID")
    @NotBlank(message = "{project.id.not_blank}")
    private String projectId;

    @Schema(description = "模块ID")
    private List<String> moduleIds;
}
