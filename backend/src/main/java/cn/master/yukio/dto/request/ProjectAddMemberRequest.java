package cn.master.yukio.dto.request;

import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 02/28/2024
 **/
@Data
public class ProjectAddMemberRequest {
    @NotBlank(message = "{project.id.not_blank}")
    private String projectId;
    @NotEmpty(message = "{user.ids.not_blank}")
    private List<@NotBlank(message = "{user_role_relation.user_id.not_blank}", groups = {Created.class, Updated.class}) String> userIds;
}
