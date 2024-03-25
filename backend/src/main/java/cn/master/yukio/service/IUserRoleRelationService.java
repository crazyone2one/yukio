package cn.master.yukio.service;

import cn.master.yukio.dto.request.GlobalUserRoleRelationQueryRequest;
import cn.master.yukio.dto.request.OrganizationUserRoleMemberEditRequest;
import cn.master.yukio.dto.user.UserExcludeOptionDTO;
import cn.master.yukio.dto.user.UserRoleRelationUserDTO;
import cn.master.yukio.dto.user.response.UserTableResponse;
import cn.master.yukio.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.UserRoleRelation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
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

    List<UserRoleRelation> selectGlobalRoleByUserId(String userId);

    void updateUserSystemGlobalRole(@Valid User user, @Valid @NotEmpty String operator, @Valid @NotEmpty List<String> roleList);

    List<UserRoleRelation> selectByUserId(String id);

    void removeByUserIdList(List<String> userIdList);

    void deleteByRoleId(String roleId);

    List<UserRoleRelation> getByRoleId(String roleId);

    List<String> getUserIdByRoleId(String roleId);

    List<UserRoleRelation> getUserIdAndSourceIdByUserIds(List<String> userIds);

    Page<UserRoleRelationUserDTO> page(GlobalUserRoleRelationQueryRequest request);

    void delete(String id);

    void removeMember(OrganizationUserRoleMemberEditRequest request);

    List<UserExcludeOptionDTO> getExcludeSelectOption(String roleId, String keyword);
}
