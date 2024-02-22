package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 用户组 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IUserRoleService extends IService<UserRole> {
    void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem);
}
