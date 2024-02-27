package cn.master.yukio.dto.user.response;

import cn.master.yukio.entity.Organization;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 02/27/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserTableResponse extends User {
    private List<Organization> organizationList = new ArrayList<>();
    private List<UserRole> userRoleList = new ArrayList<>();
}
