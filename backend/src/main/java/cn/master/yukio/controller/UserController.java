package cn.master.yukio.controller;

import cn.master.yukio.constants.UserSource;
import cn.master.yukio.dto.BasePageRequest;
import cn.master.yukio.dto.user.UserBatchCreateRequest;
import cn.master.yukio.dto.user.response.UserBatchCreateResponse;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Created;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.master.yukio.entity.User;
import cn.master.yukio.service.IUserService;
import org.springframework.web.bind.annotation.RestController;
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
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iUserService.removeById(id);
    }

    /**
     * 根据主键更新用户。
     *
     * @param user 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody User user) {
        return iUserService.updateById(user);
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
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{id}")
    public User getInfo(@PathVariable Serializable id) {
        return iUserService.getById(id);
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

}
