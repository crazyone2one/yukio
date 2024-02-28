package cn.master.yukio.controller;

import cn.master.yukio.constants.UserSource;
import cn.master.yukio.dto.BasePageRequest;
import cn.master.yukio.dto.TableBatchProcessDTO;
import cn.master.yukio.dto.TableBatchProcessResponse;
import cn.master.yukio.dto.user.UserBatchCreateRequest;
import cn.master.yukio.dto.user.UserChangeEnableRequest;
import cn.master.yukio.dto.user.UserDTO;
import cn.master.yukio.dto.user.UserEditRequest;
import cn.master.yukio.dto.user.response.UserBatchCreateResponse;
import cn.master.yukio.dto.user.response.UserSelectOption;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.User;
import cn.master.yukio.service.IUserService;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import cn.master.yukio.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用户 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService iUserService;

    /**
     * 添加用户。
     *
     * @param userCreateDTO 用户
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public UserBatchCreateResponse save(@Validated({Created.class}) @RequestBody UserBatchCreateRequest userCreateDTO) {
        return iUserService.addUser(userCreateDTO, UserSource.LOCAL.name(), SessionUtils.getUserId());
    }

    /**
     * 根据主键删除用户。
     *
     * @param request 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @PostMapping("delete")
    public TableBatchProcessResponse remove(@Validated @RequestBody TableBatchProcessDTO request) {
        return iUserService.deleteUser(request, SessionUtils.getUserId(), SessionUtils.getCurrentUser().getUsername());
    }

    /**
     * 根据主键更新用户。
     *
     * @param request 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public UserEditRequest updateUser(@Validated({Updated.class}) @RequestBody UserEditRequest request) {
        return iUserService.updateUser(request, SessionUtils.getUserId());
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<User> list() {
        return iUserService.list();
    }

    /**
     * 根据用户主键获取详细信息。
     *
     * @param keyword 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{keyword}")
    public UserDTO getInfo(@PathVariable String keyword) {
        return iUserService.getUserDtoByKeyword(keyword);
    }

    /**
     * 分页查询用户。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("/page")
    public Page<UserTableResponse> page(@Validated @RequestBody BasePageRequest request) {
        return iUserService.getUserPageList(request);
    }

    /**
     * 系统设置-系统-用户-查找系统级用户组
     *
     * @return java.util.List<cn.master.yukio.dto.user.response.UserSelectOption>
     */
    @GetMapping("/get/global/system/role")
    public List<UserSelectOption> getGlobalSystemRole() {
        return iUserService.getGlobalSystemRoleList();
    }

    /**
     * 系统设置-系统-用户-启用/禁用用户
     *
     * @param request
     * @return cn.master.yukio.dto.TableBatchProcessResponse
     */

    @PostMapping("/update/enable")
    public TableBatchProcessResponse updateUserEnable(@Validated @RequestBody UserChangeEnableRequest request) {
        return iUserService.updateUserEnable(request, SessionUtils.getUserId(), SessionUtils.getCurrentUser().getUsername());
    }

    /**
     * 系统设置-系统-用户-重置用户密码
     *
     * @param request
     * @return cn.master.yukio.dto.TableBatchProcessResponse
     */
    @PostMapping("/reset/password")
    public TableBatchProcessResponse resetPassword(@Validated @RequestBody TableBatchProcessDTO request) {
        return iUserService.resetPassword(request, SessionUtils.getUserId());
    }
}
