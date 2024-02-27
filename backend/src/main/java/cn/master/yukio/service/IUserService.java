package cn.master.yukio.service;

import cn.master.yukio.dto.BasePageRequest;
import cn.master.yukio.dto.user.UserBatchCreateRequest;
import cn.master.yukio.dto.user.response.UserBatchCreateResponse;
import cn.master.yukio.dto.user.response.UserTableResponse;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IUserService extends IService<User> {

    UserBatchCreateResponse addUser(UserBatchCreateRequest userCreateDTO, String source, String operator);

    List<User> selectUserIdByEmailList(Collection<String> emailList);

    Map<String, String> getUserNameMap(List<String> userIds);

    List<User> getUserList(String keyword);

    Page<UserTableResponse> getUserPageList(BasePageRequest request);

    List<User> selectByKeyword(@Param("keyword") String keyword, @Param("selectId") boolean selectId);
}
