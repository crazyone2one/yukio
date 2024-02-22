package cn.master.yukio.dto.project;

import cn.master.yukio.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 02/22/2024
 **/

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectRequest extends BasePageRequest {
    //@Schema(description =  "组织ID")
    private String organizationId;
    //@Schema(description =  "项目ID")
    private String projectId;
}
