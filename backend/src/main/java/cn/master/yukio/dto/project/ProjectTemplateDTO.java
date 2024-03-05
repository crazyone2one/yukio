package cn.master.yukio.dto.project;

import cn.master.yukio.entity.Template;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@Setter
@Getter
public class ProjectTemplateDTO extends Template {
    @Schema(description = "是否是默认模板")
    private Boolean enableDefault = false;

    @Schema(description = "是否是平台自动获取模板")
    private Boolean enablePlatformDefault = false;
}
