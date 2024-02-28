package cn.master.yukio.config;

import cn.master.yukio.dto.permission.PermissionDefinitionItem;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 02/28/2024
 **/
@Data
public class PermissionCache {
    private List<PermissionDefinitionItem> permissionDefinition;
}
