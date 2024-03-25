package cn.master.yukio.dto.user;

import cn.master.yukio.dto.ExcludeOptionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 03/23/2024
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExcludeOptionDTO extends ExcludeOptionDTO {
    @Schema(description =  "邮箱")
    private String email;
}
