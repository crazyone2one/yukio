package cn.master.yukio.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationMemberRequest implements Serializable {

    /**
     * 组织ID
     */
    //@Schema(description =  "组织ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{organization.id.not_blank}")
    private String organizationId;

    /**
     * 成员ID集合
     */
    //@Schema(description =  "成员ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "{user.id.not_blank}")
    private List<String> userIds;
}
