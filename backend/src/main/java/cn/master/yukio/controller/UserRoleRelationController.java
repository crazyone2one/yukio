package cn.master.yukio.controller;

import cn.master.yukio.dto.request.GlobalUserRoleRelationQueryRequest;
import cn.master.yukio.dto.user.UserExcludeOptionDTO;
import cn.master.yukio.dto.user.UserRoleRelationUserDTO;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.service.IUserRoleRelationService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 系统设置-系统-用户组-用户关联关系
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user/role/relation")
@RequiredArgsConstructor
public class UserRoleRelationController {

    private final IUserRoleRelationService iUserRoleRelationService;

    /**
     * 添加用户组关系。
     *
     * @param userRoleRelation 用户组关系
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody UserRoleRelation userRoleRelation) {
        return iUserRoleRelationService.save(userRoleRelation);
    }

    /**
     * 系统设置-系统-用户组-用户关联关系-删除全局用户组和用户的关联关系。
     *
     * @param id 主键
     */
    @GetMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        iUserRoleRelationService.delete(id);
    }

    /**
     * 根据主键更新用户组关系。
     *
     * @param userRoleRelation 用户组关系
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody UserRoleRelation userRoleRelation) {
        return iUserRoleRelationService.updateById(userRoleRelation);
    }

    /**
     * 查询所有用户组关系。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<UserRoleRelation> list() {
        return iUserRoleRelationService.list();
    }

    /**
     * 根据用户组关系主键获取详细信息。
     *
     * @param id 用户组关系主键
     * @return 用户组关系详情
     */
    @GetMapping("getInfo/{id}")
    public UserRoleRelation getInfo(@PathVariable Serializable id) {
        return iUserRoleRelationService.getById(id);
    }

    /**
     * 分页查询用户组关系。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("/global/page")
    public Page<UserRoleRelationUserDTO> page(@Validated @RequestBody GlobalUserRoleRelationQueryRequest request) {
        return iUserRoleRelationService.page(request);
    }

    @GetMapping("/global/user/option/{roleId}")
    public List<UserExcludeOptionDTO> getSelectOption(@Schema(description = "用户组ID", requiredMode = Schema.RequiredMode.REQUIRED)
                                                      @PathVariable String roleId,
                                                      @Schema(description = "查询关键字，根据邮箱和用户名查询", requiredMode = Schema.RequiredMode.REQUIRED)
                                                      @RequestParam(value = "keyword", required = false) String keyword) {
        return iUserRoleRelationService.getExcludeSelectOption(roleId, keyword);
    }
}
