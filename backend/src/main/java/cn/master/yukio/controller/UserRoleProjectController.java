package cn.master.yukio.controller;

import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.service.IUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户组 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user/role/project")
@RequiredArgsConstructor
public class UserRoleProjectController {

    private final IUserRoleService userRoleService;

    @GetMapping("/get-member/option/{projectId}/{roleId}")
    @Operation(summary = "项目管理-项目与权限-用户组-获取成员下拉选项")
    @Parameters({
            @Parameter(name = "projectId", description = "当前项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED)),
            @Parameter(name = "roleId", description = "用户组ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    })
    public List<UserExtendDTO> getMember(@PathVariable String projectId,
                                         @PathVariable String roleId,
                                         @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                         @RequestParam(required = false) String keyword) {
        return userRoleService.getMember(projectId, roleId, keyword);
    }
}
