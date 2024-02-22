package cn.master.yukio.dto.organization;

import cn.master.yukio.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationRequest extends BasePageRequest {
    private String organizationId;
}
