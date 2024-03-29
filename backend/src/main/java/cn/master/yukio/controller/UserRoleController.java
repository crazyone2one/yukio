package cn.master.yukio.controller;

import cn.master.yukio.dto.permission.PermissionDefinitionItem;
import cn.master.yukio.dto.request.OrganizationUserRoleEditRequest;
import cn.master.yukio.dto.request.OrganizationUserRoleMemberEditRequest;
import cn.master.yukio.dto.request.OrganizationUserRoleMemberRequest;
import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.service.IUserRoleRelationService;
import cn.master.yukio.service.IUserRoleService;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用户组 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user/role/organization")
@RequiredArgsConstructor
public class UserRoleController {

    private final IUserRoleService iUserRoleService;
    private final IUserRoleRelationService userRoleRelationService;

    /**
     * 添加用户组。
     *
     * @param request 用户组
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public UserRole save(@Validated({Created.class}) @RequestBody OrganizationUserRoleEditRequest request) {
        UserRole userRole = new UserRole();
        userRole.setCreateUser(SessionUtils.getUserId());
        BeanUtils.copyProperties(request, userRole);
        return iUserRoleService.add(userRole);
    }

    /**
     * 根据主键删除用户组。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @GetMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        iUserRoleService.delete(id);
    }

    /**
     * 根据主键更新用户组。
     *
     * @param request 用户组
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public UserRole update(@Validated({Updated.class}) @RequestBody OrganizationUserRoleEditRequest request) {
        UserRole userRole = new UserRole();
        userRole.setCreateUser(SessionUtils.getUserId());
        BeanUtils.copyProperties(request, userRole);
        return iUserRoleService.updateItem(userRole);
    }

    /**
     * 系统设置-组织-用户组-获取用户组列表。
     *
     * @return 所有数据
     */
    @GetMapping("/list/{organizationId}")
    public List<UserRole> list(@PathVariable String organizationId) {
        return iUserRoleService.listByOrgId(organizationId);
    }

    /**
     * 根据用户组主键获取详细信息。
     *
     * @param id 用户组主键
     * @return 用户组详情
     */
    @GetMapping("getInfo/{id}")
    public UserRole getInfo(@PathVariable Serializable id) {
        return iUserRoleService.getById(id);
    }

    /**
     * 分页查询用户组。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<UserRole> page(Page<UserRole> page) {
        return iUserRoleService.page(page);
    }

    /**
     * 系统设置-组织-用户组-获取用户组对应的权限配置
     *
     * @param id 用户组ID
     * @return java.util.List<PermissionDefinitionItem>
     */
    @GetMapping("/permission/setting/{id}")
    public List<PermissionDefinitionItem> getPermissionSetting(@PathVariable String id) {
        return iUserRoleService.getPermissionSetting(id);
    }

    /**
     * 系统设置-系统-用户组-获取全局用户组列表
     *
     * @return java.util.List<cn.master.yukio.entity.UserRole>
     */
    @GetMapping("/global/list")
    public List<UserRole> list() {
        return iUserRoleService.globalList();
    }

    /**
     * 系统设置-组织-用户组-获取成员列表
     *
     * @param request
     * @return com.mybatisflex.core.paginate.Page<cn.master.yukio.entity.User>
     */
    @PostMapping("/list-member")
    public Page<User> listMember(@Validated @RequestBody OrganizationUserRoleMemberRequest request) {
        return iUserRoleService.listMember(request);
    }

    @PostMapping("/remove-member")
    public void removeMember(@Validated @RequestBody OrganizationUserRoleMemberEditRequest request) {
        userRoleRelationService.removeMember(request);
    }

    /**
     * 系统设置-组织-用户组-获取成员下拉选项
     *
     * @param organizationId 组织ID
     * @param roleId 用户组ID
     * @param keyword 关键字
     * @return java.util.List<cn.master.yukio.dto.user.UserExtendDTO>
     */
    @GetMapping("/get-member/option/{organizationId}/{roleId}")
    public List<UserExtendDTO> getMember(@PathVariable String organizationId,
                                         @PathVariable String roleId,
                                         @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                         @RequestParam(required = false) String keyword) {
        return iUserRoleService.getMember(organizationId, roleId, keyword);
    }
}
