package cn.master.yukio.dto.organization;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationMemberBatchRequest extends OrganizationMemberRequest{

    /**
     * 组织ID集合
     */
    //@Schema(description =  "组织ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "{organization.id.not_blank}")
    private List<String> organizationIds;
}
