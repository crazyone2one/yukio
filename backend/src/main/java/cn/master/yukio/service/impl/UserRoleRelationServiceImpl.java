package cn.master.yukio.service.impl;

import cn.master.yukio.constants.UserRoleEnum;
import cn.master.yukio.constants.UserRoleScope;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.Organization;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.mapper.UserRoleRelationMapper;
import cn.master.yukio.service.IUserRoleRelationService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.yukio.entity.table.UserRoleTableDef.USER_ROLE;

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

    @Override
    public Map<String, UserTableResponse> selectGlobalUserRoleAndOrganization(List<String> userIdList) {
        List<UserRoleRelation> userRoleRelationList = selectGlobalRoleByUserIdList(userIdList);
        List<String> userRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).distinct().toList();
        List<String> sourceIdList = userRoleRelationList.stream().map(UserRoleRelation::getSourceId).distinct().toList();
        Map<String, UserRole> userRoleMap = new HashMap<>();
        Map<String, Organization> organizationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userRoleIdList)) {
            userRoleMap = QueryChain.of(UserRole.class).where(USER_ROLE.ID.in(userRoleIdList)
                            .and(USER_ROLE.SCOPE_ID.eq(UserRoleEnum.GLOBAL.toString())))
                    .list().stream().collect(Collectors.toMap(UserRole::getId, item -> item));
        }
        if (CollectionUtils.isNotEmpty(sourceIdList)) {
            organizationMap = QueryChain.of(Organization.class).where(ORGANIZATION.ID.in(sourceIdList)).list()
                    .stream().collect(Collectors.toMap(Organization::getId, item -> item));
        }
        Map<String, UserTableResponse> returnMap = new HashMap<>();
        for (UserRoleRelation userRoleRelation : userRoleRelationList) {
            UserTableResponse userInfo = returnMap.get(userRoleRelation.getUserId());
            if (userInfo == null) {
                userInfo = new UserTableResponse();
                userInfo.setId(userRoleRelation.getUserId());
                returnMap.put(userRoleRelation.getUserId(), userInfo);
            }
            UserRole userRole = userRoleMap.get(userRoleRelation.getRoleId());
            if (userRole != null && StringUtils.equalsIgnoreCase(userRole.getType(), UserRoleScope.SYSTEM)) {
                userInfo.getUserRoleList().add(userRole);
            }
            Organization organization = organizationMap.get(userRoleRelation.getSourceId());
            if (organization != null && !userInfo.getOrganizationList().contains(organization)) {
                userInfo.getOrganizationList().add(organization);
            }
        }
        return returnMap;
    }

    @Override
    public List<UserRoleRelation> selectGlobalRoleByUserIdList(List<String> userIdList) {
        List<UserRole> list = QueryChain.of(UserRole.class).select("id").from(USER_ROLE).where(USER_ROLE.SCOPE_ID.eq("global")).list();
        return QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.in(userIdList)
                .and(USER_ROLE_RELATION.ROLE_ID.in(list.stream().map(UserRole::getId).toList()))).list();
    }
}
