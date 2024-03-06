package cn.master.yukio.service.impl;

import cn.master.yukio.constants.InternalUserRole;
import cn.master.yukio.constants.UserRoleScope;
import cn.master.yukio.constants.UserRoleType;
import cn.master.yukio.dto.BasePageRequest;
import cn.master.yukio.dto.OptionDTO;
import cn.master.yukio.dto.TableBatchProcessDTO;
import cn.master.yukio.dto.TableBatchProcessResponse;
import cn.master.yukio.dto.user.*;
import cn.master.yukio.dto.user.response.UserBatchCreateResponse;
import cn.master.yukio.dto.user.response.UserSelectOption;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRole;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.UserMapper;
import cn.master.yukio.service.*;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.DistinctQueryColumn;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.ProjectTableDef.PROJECT;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.yukio.entity.table.UserRoleTableDef.USER_ROLE;
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

    @Override
    public List<UserSelectOption> getGlobalSystemRoleList() {
        List<UserSelectOption> returnList = new ArrayList<>();
        List<UserRole> list = QueryChain.of(UserRole.class).where(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL)
                .and(USER_ROLE.TYPE.eq(UserRoleType.SYSTEM.name()))).list();
        list.forEach(userRole -> {
            UserSelectOption userRoleOption = new UserSelectOption();
            userRoleOption.setId(userRole.getId());
            userRoleOption.setValue(userRole.getId());
            userRoleOption.setLabel(userRole.getName());
            userRoleOption.setName(userRole.getName());
            userRoleOption.setSelected(StringUtils.equals(userRole.getId(), InternalUserRole.MEMBER.getValue()));
            userRoleOption.setCloseable(!StringUtils.equals(userRole.getId(), InternalUserRole.MEMBER.getValue()));
            returnList.add(userRoleOption);
        });
        return returnList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEditRequest updateUser(UserEditRequest request, String operator) {
        userRoleService.checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        checkUserEmail(request.getId(), request.getEmail());
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setUpdateUser(operator);
        mapper.update(user);
        userRoleRelationService.updateUserSystemGlobalRole(user, user.getUpdateUser(), request.getUserRoleIdList());
        return request;
    }

    @Override
    public UserDTO getUserDtoByKeyword(String keyword) {
        UserDTO userDTO = queryChain().where(USER.ID.eq(keyword).or(USER.EMAIL.eq(keyword))).oneAs(UserDTO.class);
        if (Objects.nonNull(userDTO)) {
            userDTO.setUserRoleRelations(userRoleRelationService.selectByUserId(userDTO.getId()));
            userDTO.setUserRoles(userRoleService.selectByUserRoleRelations(userDTO.getUserRoleRelations()));
        }
        return userDTO;
    }

    @Override
    public UserDTO getUserDtoByName(String name) {
        UserDTO userDTO = queryChain().where(USER.NAME.eq(name)).oneAs(UserDTO.class);
        if (Objects.nonNull(userDTO)) {
            userDTO.setUserRoleRelations(userRoleRelationService.selectByUserId(userDTO.getId()));
            userDTO.setUserRoles(userRoleService.selectByUserRoleRelations(userDTO.getUserRoleRelations()));
        }
        return userDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse deleteUser(TableBatchProcessDTO request, String operatorId, String operatorName) {
        List<String> userIdList = getBatchUserIds(request);
        checkUserInDb(userIdList);
        //检查是否含有Admin
        this.checkProcessUserAndThrowException(userIdList, operatorId, operatorName, Translator.get("user.not.delete"));
        // 更新删除标志位
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(userIdList.size());
        response.setSuccessCount(this.deleteUserByList(userIdList, operatorId));
        //删除用户角色关系
        userRoleRelationService.removeByUserIdList(userIdList);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse updateUserEnable(UserChangeEnableRequest request, String operatorId, String operatorName) {
        request.setSelectIds(getBatchUserIds(request));
        checkUserInDb(request.getSelectIds());
        if (!request.isEnable()) {
            this.checkProcessUserAndThrowException(request.getSelectIds(), operatorId, operatorName, Translator.get("user.not.disable"));
        }
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(request.getSelectIds().size());
        List<User> userList = queryChain().where(USER.ID.in(request.getSelectIds())).list();
        response.setSuccessCount(this.enAbleUserByList(userList, operatorId, request));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse resetPassword(TableBatchProcessDTO request, String operator) {
        request.setSelectIds(getBatchUserIds(request));
        this.checkUserInDb(request.getSelectIds());
        //int insertIndex = 0;
        List<User> userList = mapper.selectListByIds(request.getSelectIds());
        for (User user : userList) {
            User updateModel = UpdateEntity.of(User.class, user.getId());
            updateModel.setUpdateUser(operator);
            if (StringUtils.equalsIgnoreCase("admin", user.getId())) {
                updateModel.setPassword(passwordEncoder.encode("admin"));
            } else {
                updateModel.setPassword(passwordEncoder.encode(user.getEmail()));
            }
            mapper.update(updateModel);
            //insertIndex++;
        }
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(request.getSelectIds().size());
        response.setSuccessCount(request.getSelectIds().size());
        return response;
    }

    @Override
    public UserDTO switchWorkspace(String sign, String sourceId) {
        UserDTO user = getUserDtoByName(SessionUtils.getCurrentUser().getUsername());
        User newUser = new User();
        if (StringUtils.equals("workspace", sign)) {
            user.setLastOrganizationId(sourceId);
            user.setLastProjectId(StringUtils.EMPTY);
            List<Project> projects = getProjectListByWsAndUserId(SessionUtils.getUserId(), sourceId);
            if (CollectionUtils.isNotEmpty(projects)) {
                user.setLastProjectId(projects.get(0).getId());
            } else {
                user.setLastProjectId(StringUtils.EMPTY);
            }
        }
        BeanUtils.copyProperties(user, newUser);
        mapper.update(newUser);
        return getUserDtoByName(user.getName());
    }

    private List<Project> getProjectListByWsAndUserId(String userId, String sourceId) {
        List<Project> projectList = new ArrayList<>();
        List<Project> projects = QueryChain.of(Project.class).where(PROJECT.ORGANIZATION_ID.eq(sourceId)).list();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)).list();
        userRoleRelations.forEach(userRoleRelation -> {
            projects.forEach(project -> {
                if (StringUtils.equals(userRoleRelation.getSourceId(), project.getId())) {
                    projectList.add(project);
                }
            });
        });
        return projectList;
    }

    private long enAbleUserByList(List<User> userList, String operatorId, UserChangeEnableRequest request) {
        long insertIndex = 0;
        for (User user : userList) {
            user.setEnable(request.isEnable());
            user.setUpdateUser(operatorId);
            mapper.update(user);
            insertIndex++;
        }
        return insertIndex;
    }

    private long deleteUserByList(List<String> updateUserList, String operator) {
        long insertIndex = 0;
        for (String userId : updateUserList) {
            User user = UpdateEntity.of(User.class, userId);
            user.setDeleted(true);
            user.setUpdateUser(operator);
            user.setEmail(userId);
            mapper.update(user);
            insertIndex++;
        }
        return insertIndex;
    }

    private void checkProcessUserAndThrowException(List<String> userIdList, String operatorId, String operatorName, String exceptionMessage) {
        for (String userId : userIdList) {
            if (StringUtils.equalsAny(userId, "admin", operatorId)) {
                throw new MSException(exceptionMessage + ":" + operatorName);
            }
        }
    }

    private void checkUserInDb(List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new MSException(Translator.get("user.not.exist"));
        }
        List<User> userInDb = queryChain().where(USER.ID.in(userIdList)).list();
        if (userIdList.size() != userInDb.size()) {
            throw new MSException(Translator.get("user.not.exist"));
        }
    }

    private List<String> getBatchUserIds(TableBatchProcessDTO request) {
        if (request.isSelectAll()) {
            List<User> userList = queryChain().select(USER.ID).where(USER.ID.eq(request.getCondition().getKeyword())
                    .or(USER.NAME.like(request.getCondition().getKeyword()))
                    .or(USER.EMAIL.like(request.getCondition().getKeyword()))).list();
            List<String> userIdList = new ArrayList<>(userList.stream().map(User::getId).toList());
            if (CollectionUtils.isNotEmpty(request.getExcludeIds())) {
                userIdList.removeAll(request.getExcludeIds());
            }
            return userIdList;
        }
        return request.getSelectIds();
    }

    private void checkUserEmail(String id, String email) {
        if (queryChain().where(USER.EMAIL.eq(email).and(USER.ID.ne(id))).exists()) {
            throw new MSException(Translator.get("user_email_already_exists"));
        }
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
