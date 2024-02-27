package cn.master.yukio.service;

import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.User;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.UserRoleRelation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户组关系 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IUserRoleRelationService extends IService<UserRoleRelation> {
    void batchSave(List<String> userRoleIdList, List<User> userList);

    void batchSave(List<String> userRoleIdList, User user);

    Map<String, UserTableResponse> selectGlobalUserRoleAndOrganization(@Valid @NotEmpty List<String> userIdList);

    List<UserRoleRelation> selectGlobalRoleByUserIdList(@Param("userIds") List<String> userIdList);
}
