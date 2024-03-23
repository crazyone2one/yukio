package cn.master.yukio.dto.organization;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 03/22/2024
 **/
@Setter
@Getter
public class OrganizationSwitchRequest {
    private String organizationId;
    private String userId;
}
