package cn.master.yukio.service;

import cn.master.yukio.entity.User;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.UserRoleRelation;

import java.util.List;

/**
 * 用户组关系 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IUserRoleRelationService extends IService<UserRoleRelation> {
    void batchSave(List<String> userRoleIdList, List<User> userList);

    void batchSave(List<String> userRoleIdList, User user);
}
