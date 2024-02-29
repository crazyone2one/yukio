package cn.master.yukio.controller;

import cn.master.yukio.dto.permission.PermissionDefinitionItem;
import cn.master.yukio.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
