package cn.master.yukio.service;

import cn.master.yukio.dto.organization.*;
import cn.master.yukio.dto.user.UserExtendDTO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.Organization;

/**
 * 组织 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IOrganizationService extends IService<Organization> {

    void updateOrg(OrganizationDTO organizationDTO);

    void updateName(OrganizationDTO organizationDTO);

    void delete(OrganizationDeleteRequest organizationDeleteRequest);

    void recover(String id);

    void enable(String id);

    void disable(String id);

    Page<UserExtendDTO> getMemberListBySystem(OrganizationRequest request);

    void addMemberBySystem(OrganizationMemberRequest request, String createUserId);

    void addMemberBySystem(OrganizationMemberBatchRequest batchRequest, String createUserId);

    void removeMember(String organizationId, String userId, String currentUser);

    OrganizationDTO getDefault();
}
