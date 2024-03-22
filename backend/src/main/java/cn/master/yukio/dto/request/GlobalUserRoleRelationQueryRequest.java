package cn.master.yukio.dto.request;

import cn.master.yukio.dto.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 03/22/2024
 **/
@Setter
@Getter
public class GlobalUserRoleRelationQueryRequest extends BasePageRequest {
    @NotBlank
    @Schema(description =  "用户组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleId;
}
