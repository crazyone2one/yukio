package cn.master.yukio.dto.permission;

import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 02/28/2024
 **/
@Data
public class PermissionDefinitionItem {
    //@Schema(description =  "菜单项ID")
    private String id;
    //@Schema(description =  "菜单所属类型 SYSTEM ORGANIZATION PROJECT")
    private String type;
    //@Schema(description =  "菜单项名称")
    private String name;
    //@Schema(description =  "是否是企业版菜单")
    private Boolean license = false;
    //@Schema(description =  "菜单是否全选")
    private Boolean enable = false;
    //@Schema(description =  "菜单下的权限列表")
    private List<Permission> permissions;
    //@Schema(description =  "子菜单")
    private List<PermissionDefinitionItem> children;
}
