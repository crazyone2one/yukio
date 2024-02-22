package cn.master.yukio.dto.user.response;

import cn.master.yukio.dto.user.UserCreateInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
public class UserBatchCreateResponse {
    List<UserCreateInfo> successList;
    Map<String, String> errorEmails;
}
