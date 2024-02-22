package cn.master.yukio.dto.organization;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationDeleteRequest implements Serializable {

    /**
     * 删除组织ID
     */
    private String organizationId;

    /**
     * 删除人ID
     */
    private String deleteUserId;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}
