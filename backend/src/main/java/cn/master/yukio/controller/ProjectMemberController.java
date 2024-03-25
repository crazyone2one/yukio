package cn.master.yukio.controller;

import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by 11's papa on 03/23/2024
 **/
@RestController
@RequestMapping("/project/member")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping("/get-member/option/{projectId}")
    @Operation(summary = "项目管理-成员-获取成员下拉选项")
    public List<UserExtendDTO> getMemberOption(@PathVariable String projectId,
                                               @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                               @RequestParam(value = "keyword", required = false) String keyword) {
        return projectMemberService.getMemberOption(projectId, keyword);
    }
}
