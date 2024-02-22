package cn.master.yukio.service.impl;

import cn.master.yukio.constants.UserRoleScope;
import cn.master.yukio.entity.User;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.mapper.UserRoleRelationMapper;
import cn.master.yukio.service.IUserRoleRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements IUserRoleRelationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<String> userRoleIdList, List<User> userList) {
        List<UserRoleRelation> userRoleRelationSaveList = new ArrayList<>();
        for (String userRoleId : userRoleIdList) {
            for (User user : userList) {
                UserRoleRelation userRoleRelation = UserRoleRelation.builder().roleId(userRoleId).userId(user.getId())
                        .createUser(user.getCreateUser())
                        .sourceId(UserRoleScope.SYSTEM)
                        .organizationId(UserRoleScope.SYSTEM)
                        .build();
                userRoleRelationSaveList.add(userRoleRelation);
            }
        }
        mapper.insertBatch(userRoleRelationSaveList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<String> userRoleIdList, User user) {
        List<UserRoleRelation> userRoleRelationSaveList = new ArrayList<>();
        for (String userRoleId : userRoleIdList) {
            UserRoleRelation userRoleRelation = UserRoleRelation.builder().roleId(userRoleId)
                    .userId(user.getId())
                    .createUser(user.getCreateUser())
                    .sourceId(UserRoleScope.SYSTEM)
                    .organizationId(UserRoleScope.SYSTEM)
                    .build();
            userRoleRelationSaveList.add(userRoleRelation);
        }
        mapper.insertBatch(userRoleRelationSaveList);
    }
}
