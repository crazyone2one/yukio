package cn.master.yukio.dto.user;

import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
public class UserBatchCreateRequest {
    @NotEmpty(groups = {Created.class, Updated.class}, message = "{user.info.not_empty}")
    List<@Valid UserCreateInfo> userInfoList;
    @NotEmpty(groups = {Created.class, Updated.class}, message = "{user_role.id.not_blank}")
    List<@Valid @NotBlank(message = "{user_role.id.not_blank}", groups = {Created.class, Updated.class}) String> userRoleIdList;
}
