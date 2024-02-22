package cn.master.yukio.dto.organization;

import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationNameEditRequest implements Serializable {

    //@Schema(description =  "组织ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    //@Schema(description =  "组织名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{organization.name.not_blank}", groups = {Created.class, Updated.class})
    @Size(min = 1, max = 255, message = "{organization.name.length_range}", groups = {Created.class, Updated.class})
    private String name;
}
