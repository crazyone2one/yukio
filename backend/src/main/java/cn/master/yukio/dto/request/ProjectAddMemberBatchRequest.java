package cn.master.yukio.dto.request;

import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 02/28/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectAddMemberBatchRequest extends ProjectAddMemberRequest {
    private List<@NotBlank(message = "{project.id.not_blank}", groups = {Created.class, Updated.class}) String> projectIds;
}
