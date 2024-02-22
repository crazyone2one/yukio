package cn.master.yukio.dto.user;

import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
public class UserCreateInfo {
    private String id;
    @NotBlank(message = "{user.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{user.name.length_range}", groups = {Created.class, Updated.class})
    private String name;
    private String email;
    private String phone;
}
