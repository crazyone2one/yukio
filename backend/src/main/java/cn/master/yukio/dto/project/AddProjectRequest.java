package cn.master.yukio.dto.project;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class AddProjectRequest extends ProjectBaseRequest {

    //@Schema(description =  "项目ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, max = 50, message = "{project.id.length_range}")
    private String id;
}
