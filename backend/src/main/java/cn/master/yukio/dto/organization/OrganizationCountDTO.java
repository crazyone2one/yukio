package cn.master.yukio.dto.organization;

import lombok.Data;

/**
 * @author Created by 11's papa on 02/25/2024
 **/
@Data
public class OrganizationCountDTO {
    /**
     * 成员数量
     */
    private Integer memberCount;

    /**
     * 项目数量
     */
    private Integer projectCount;

    /**
     * 组织ID
     */
    private String id;
}
