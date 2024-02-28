package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.UserRolePermission;
import cn.master.yukio.mapper.UserRolePermissionMapper;
import cn.master.yukio.service.IUserRolePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission> implements IUserRolePermissionService {

    @Override
    public List<UserRolePermission> getByRoleId(String roleId) {
        return queryChain().where(USER_ROLE_PERMISSION.ROLE_ID.eq(roleId)).list();
    }

    @Override
    public Set<String> getPermissionIdSetByRoleId(String roleId) {
        return getByRoleId(roleId).stream().map(UserRolePermission::getPermissionId).collect(Collectors.toSet());
    }
}
