package cn.master.yukio.service;

import cn.master.yukio.dto.permission.PermissionDefinitionItem;
import cn.master.yukio.entity.UserRoleRelation;
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

    List<UserRole> selectByUserRoleRelations(List<UserRoleRelation> userRoleRelations);

    List<UserRole> listByOrgId(String organizationId);

    UserRole add(UserRole userRole);

    void checkNewRoleExist(UserRole userRole);

    UserRole updateItem(UserRole userRole);

    void checkGlobalUserRole(UserRole userRole);

    void delete(String id);

    /**
     * 校验是否是内置用户组，是内置抛异常
     *
     * @param userRole
     */
    void checkInternalUserRole(UserRole userRole);

    List<PermissionDefinitionItem> getPermissionSetting(String id);

    List<PermissionDefinitionItem> getPermissionSetting(UserRole userRole);

    List<UserRole> globalList();
}
