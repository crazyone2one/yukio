package cn.master.yukio.dto;

import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 02/27/2024
 **/
@Data
public class UserRolePermission implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "{user_role_permission.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 64, message = "{user_role_permission.id.length_range}", groups = {Created.class, Updated.class})
    private String id;
    @NotBlank(message = "{user_role_permission.role_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 64, message = "{user_role_permission.role_id.length_range}", groups = {Created.class, Updated.class})
    private String roleId;
    @NotBlank(message = "{user_role_permission.permission_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 128, message = "{user_role_permission.permission_id.length_range}", groups = {Created.class, Updated.class})
    private String permissionId;

}
