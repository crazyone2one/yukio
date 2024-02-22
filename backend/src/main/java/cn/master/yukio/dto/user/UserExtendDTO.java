package cn.master.yukio.dto.user;

import cn.master.yukio.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class UserExtendDTO extends User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 是否管理员(组织, 项目)
     */

    private boolean adminFlag;

    /**
     * 是否成员(组织, 项目)
     */
    //@Schema(description = "是否组织/项目成员, 是: 勾选禁用, 否: 勾选启用")
    private boolean memberFlag;

    /**
     * 是否勾选用户组
     */
    //@Schema(description = "是否属于用户组, 是: 勾选禁用, 否: 勾选启用")
    private boolean checkRoleFlag;

    //@Schema(description = "组织ID")
    private String sourceId;
}
