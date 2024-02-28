package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.UserRolePermission;

import java.util.List;
import java.util.Set;

/**
 * 用户组权限 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IUserRolePermissionService extends IService<UserRolePermission> {
    List<UserRolePermission> getByRoleId(String roleId);

    Set<String> getPermissionIdSetByRoleId(String roleId);
}
