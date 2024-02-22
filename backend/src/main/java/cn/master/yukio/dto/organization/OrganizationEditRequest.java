package cn.master.yukio.dto.organization;

import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationEditRequest implements Serializable {

    //@Schema(description = "组织ID")
    private String id;

    //@Schema(description = "组织名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{organization.name.not_blank}", groups = {Created.class, Updated.class})
    @Size(min = 1, max = 255, message = "{organization.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    //@Schema(description =  "描述")
    @Size(max = 500, groups = {Created.class, Updated.class})
    private String description;

    //@Schema(description = "成员ID集合")
    @NotEmpty(message = "{project.member_count.not_blank}", groups = {Created.class, Updated.class})
    private List<String> userIds;
}
