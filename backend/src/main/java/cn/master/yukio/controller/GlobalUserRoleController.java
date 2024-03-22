package cn.master.yukio.controller;

import cn.master.yukio.dto.permission.PermissionDefinitionItem;
import cn.master.yukio.dto.system.UserRoleUpdateRequest;
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.service.IUserRoleService;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统设置-系统-用户组
 *
 * @author Created by 11's papa on 02/29/2024
 **/
@RestController
@RequestMapping("/user/role/global")
@RequiredArgsConstructor
public class GlobalUserRoleController {
    private final IUserRoleService userRoleService;

    @GetMapping("/permission/setting/{id}")
    public List<PermissionDefinitionItem> getPermissionSetting(@PathVariable String id) {
        return userRoleService.getPermissionSetting(id);
    }

    /**
     * 系统设置-系统-用户组-获取全局用户组列表
     *
     * @return java.util.List<cn.master.yukio.entity.UserRole>
     */
    @GetMapping("/list")
    public List<UserRole> list() {
        return userRoleService.globalList();
    }

    @PostMapping("/add")
    public UserRole add(@Validated({Created.class}) @RequestBody UserRoleUpdateRequest request) {
        UserRole userRole = new UserRole();
        userRole.setCreateUser(SessionUtils.getUserId());
        BeanUtils.copyProperties(request, userRole);
        return userRoleService.sysAdd(userRole);
    }
}
