package cn.master.yukio.dto.user;

import cn.master.yukio.dto.TableBatchProcessDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 02/27/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserChangeEnableRequest extends TableBatchProcessDTO {
    boolean enable;
}
