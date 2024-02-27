package cn.master.yukio.service.impl;

import cn.master.yukio.dto.BasePageRequest;
import cn.master.yukio.dto.OptionDTO;
import cn.master.yukio.dto.user.UserBatchCreateRequest;
import cn.master.yukio.dto.user.UserCreateInfo;
import cn.master.yukio.dto.user.response.UserBatchCreateResponse;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.User;
import cn.master.yukio.mapper.UserMapper;
import cn.master.yukio.service.*;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.DistinctQueryColumn;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.UserTableDef.USER;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final IUserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final IUserRoleRelationService userRoleRelationService;
    private final IOperationLogService operationLogService;
    private final IUserLogService userLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserBatchCreateResponse addUser(UserBatchCreateRequest userCreateDTO, String source, String operator) {
        userRoleService.checkRoleIsGlobalAndHaveMember(userCreateDTO.getUserRoleIdList(), true);
        UserBatchCreateResponse response = new UserBatchCreateResponse();
        //检查用户邮箱的合法性
        Map<String, String> errorEmails = validateUserInfo(userCreateDTO.getUserInfoList().stream().map(UserCreateInfo::getEmail).toList());
        if (MapUtils.isNotEmpty(errorEmails)) {
            response.setErrorEmails(errorEmails);
        } else {
            response.setSuccessList(saveUserAndRole(userCreateDTO, source, operator, "/system/user/addUser"));
        }
        return response;
    }

    private List<UserCreateInfo> saveUserAndRole(UserBatchCreateRequest userCreateDTO, String source, String operator, String requestPath) {
        List<UserCreateInfo> insertList = new ArrayList<>();
        List<User> saveUserList = new ArrayList<>();
        for (UserCreateInfo userInfo : userCreateDTO.getUserInfoList()) {
            User user = new User();
            BeanUtils.copyProperties(userInfo, user);
            user.setSource(source);
            user.setCreateUser(operator);
            user.setUpdateUser(operator);
            user.setPassword(passwordEncoder.encode(user.getEmail()));
            mapper.insert(user);
            saveUserList.add(user);
            insertList.add(userInfo);
        }
        userRoleRelationService.batchSave(userCreateDTO.getUserRoleIdList(), saveUserList);
        operationLogService.batchAdd(userLogService.getBatchAddLogs(saveUserList, requestPath));
        return insertList;
    }

    @Override
    public List<User> selectUserIdByEmailList(Collection<String> emailList) {
        return this.queryChain().select(USER.ID, USER.EMAIL).where(USER.EMAIL.in(emailList)).list();
    }

    @Override
    public Map<String, String> getUserNameMap(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        return getSelectOptionByIdsWithDeleted(userIds).stream().collect(Collectors.toMap(OptionDTO::getId, OptionDTO::getName));
    }

    @Override
    public List<User> getUserList(String keyword) {
        return QueryChain.of(User.class).select(new DistinctQueryColumn(USER.ID, USER.EMAIL, USER.NAME))
                .where(USER.NAME.like(keyword).or(USER.EMAIL.like(keyword))).orderBy(USER.CREATE_TIME.desc()).limit(100).list();
    }

    @Override
    public Page<UserTableResponse> getUserPageList(BasePageRequest request) {
        String keyword = request.getKeyword();
        QueryChain<User> queryChain = QueryChain.of(User.class).select(USER.ALL_COLUMNS)
                .where(USER.NAME.like(keyword).or(USER.EMAIL.like(keyword)).or(USER.ID.eq(keyword)));
        Page<UserTableResponse> responsePage = mapper.paginateAs(Page.of(request.getCurrent(), request.getPageSize()), queryChain, UserTableResponse.class);
        List<UserTableResponse> userList = responsePage.getRecords();
        if (CollectionUtils.isNotEmpty(userList)) {
            List<String> userIdList = userList.stream().map(User::getId).toList();
            Map<String, UserTableResponse> roleAndOrganizationMap = userRoleRelationService.selectGlobalUserRoleAndOrganization(userIdList);
            userList.forEach(user -> {
                UserTableResponse roleOrgModel = roleAndOrganizationMap.get(user.getId());
                if (roleOrgModel != null) {
                    user.setUserRoleList(roleOrgModel.getUserRoleList());
                    user.setOrganizationList(roleOrgModel.getOrganizationList());
                }
            });
        }
        return responsePage;
    }

    @Override
    public List<User> selectByKeyword(String keyword, boolean selectId) {
        QueryChain<User> queryChain = QueryChain.of(User.class);
        if (selectId) {
            queryChain.select(USER.ID);
        } else {
            queryChain.select(USER.ALL_COLUMNS);
        }
        queryChain.where(USER.NAME.like(keyword).or(USER.EMAIL.like(keyword)).or(USER.ID.eq(keyword)));
        return mapper.selectListByQuery(queryChain);
    }

    private List<OptionDTO> getSelectOptionByIdsWithDeleted(List<String> ids) {
        return this.queryChain().select(USER.ID, USER.NAME).where(USER.ID.in(ids)).listAs(OptionDTO.class);
    }

    private Map<String, String> validateUserInfo(Collection<String> createEmails) {
        Map<String, String> errorMessage = new HashMap<>();
        String userEmailRepeatError = Translator.get("user.email.repeat");
        //判断参数内是否含有重复邮箱
        List<String> emailList = new ArrayList<>();
        Map<String, String> userInDbMap = selectUserIdByEmailList(createEmails).stream().collect(Collectors.toMap(User::getEmail, User::getId));
        for (String createEmail : createEmails) {
            if (emailList.contains(createEmail)) {
                errorMessage.put(createEmail, userEmailRepeatError);
            } else {
                //判断邮箱是否已存在数据库中
                if (userInDbMap.containsKey(createEmail)) {
                    errorMessage.put(createEmail, userEmailRepeatError);
                } else {
                    emailList.add(createEmail);
                }
            }
        }
        return errorMessage;
    }
}
