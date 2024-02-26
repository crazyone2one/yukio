package cn.master.yukio.controller;

import cn.master.yukio.dto.organization.*;
import cn.master.yukio.dto.project.OrganizationProjectRequest;
import cn.master.yukio.dto.project.ProjectDTO;
import cn.master.yukio.dto.project.ProjectRequest;
import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.Organization;
import cn.master.yukio.service.IOrganizationService;
import cn.master.yukio.service.IProjectService;
import cn.master.yukio.util.SessionUtils;
import cn.master.yukio.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 组织 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/system/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final IOrganizationService iOrganizationService;
    private final IProjectService projectService;

    @PostMapping("/add")
    public void add(@Validated({Updated.class}) @RequestBody OrganizationEditRequest organizationEditRequest) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        BeanUtils.copyProperties(organizationEditRequest, organizationDTO);
        organizationDTO.setCreateUser(SessionUtils.getUserId());
        organizationDTO.setUpdateUser(SessionUtils.getUserId());
        iOrganizationService.addOrg(organizationDTO);
    }

    @PostMapping("/update")
    public void update(@Validated({Updated.class}) @RequestBody OrganizationEditRequest organizationEditRequest) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        BeanUtils.copyProperties(organizationEditRequest, organizationDTO);
        organizationDTO.setUpdateUser(SessionUtils.getUserId());
        iOrganizationService.updateOrg(organizationDTO);
    }

    @PostMapping("/rename")
    public void rename(@Validated({Updated.class}) @RequestBody OrganizationNameEditRequest organizationEditRequest) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        BeanUtils.copyProperties(organizationEditRequest, organizationDTO);
        organizationDTO.setUpdateUser(SessionUtils.getUserId());
        iOrganizationService.updateName(organizationDTO);
    }

    /**
     * 系统设置-系统-组织与项目-组织-恢复组织
     *
     * @param id organization id
     */
    @GetMapping("/recover/{id}")
    public void recover(@PathVariable String id) {
        iOrganizationService.recover(id);
    }

    /**
     * 系统设置-系统-组织与项目-组织-启用组织
     *
     * @param id organization id
     */
    @GetMapping("/enable/{id}")
    public void enable(@PathVariable String id) {
        iOrganizationService.enable(id);
    }

    /**
     * 系统设置-系统-组织与项目-组织-结束组织
     *
     * @param id organization id
     */
    @GetMapping("/disable/{id}")
    public void disable(@PathVariable String id) {
        iOrganizationService.disable(id);
    }

    /**
     * 系统设置-系统-组织与项目-组织-获取组织成员列表
     *
     * @param request
     * @return com.mybatisflex.core.paginate.Page<cn.master.yukio.dto.user.UserExtendDTO>
     */

    @PostMapping("/list-member")
    public Page<UserExtendDTO> listMember(@Validated @RequestBody OrganizationRequest request) {
        return iOrganizationService.getMemberListBySystem(request);
    }

    /**
     * 根据主键删除组织。
     *
     * @param id 主键
     */
    @DeleteMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        OrganizationDeleteRequest organizationDeleteRequest = new OrganizationDeleteRequest();
        organizationDeleteRequest.setOrganizationId(id);
        organizationDeleteRequest.setDeleteUserId(SessionUtils.getUserId());
        organizationDeleteRequest.setDeleteTime(LocalDateTime.now());
        iOrganizationService.delete(organizationDeleteRequest);
    }

    /**
     * 查询所有组织。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Organization> list() {
        return iOrganizationService.list();
    }

    /**
     * 根据组织主键获取详细信息。
     *
     * @param id 组织主键
     * @return 组织详情
     */
    @GetMapping("getInfo/{id}")
    public Organization getInfo(@PathVariable Serializable id) {
        return iOrganizationService.getById(id);
    }

    /**
     * 分页查询组织。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<OrganizationDTO> page(@Validated @RequestBody OrganizationRequest request) {
        return iOrganizationService.findPagesByRequest(request);
    }

    /**
     * 系统设置-系统-组织与项目-组织-添加组织成员
     *
     * @param request
     */
    @PostMapping("/add-member")
    public void addMember(@Validated @RequestBody OrganizationMemberRequest request) {
        iOrganizationService.addMemberBySystem(request, SessionUtils.getUserId());
    }

    /**
     * 系统设置-系统-组织与项目-组织-删除组织成员
     *
     * @param organizationId organizationId
     * @param userId         userId
     */
    @GetMapping("/remove-member/{organizationId}/{userId}")
    public void removeMember(@PathVariable String organizationId, @PathVariable String userId) {
        iOrganizationService.removeMember(organizationId, userId, SessionUtils.getUserId());
    }

    /**
     * 系统设置-系统-组织与项目-组织-获取系统默认组织
     *
     * @return cn.master.yukio.dto.organization.OrganizationDTO
     */
    @GetMapping("/default")
    public OrganizationDTO getDefault() {
        return iOrganizationService.getDefault();
    }

    /**
     * 系统设置-系统-组织与项目-组织-获取组织下的项目列表
     *
     * @param request
     * @return com.mybatisflex.core.paginate.Page<cn.master.yukio.dto.project.ProjectDTO>
     */
    @PostMapping("/list-project")
    public Page<ProjectDTO> page(@Validated @RequestBody OrganizationProjectRequest request) {
        ProjectRequest projectRequest = new ProjectRequest();
        BeanUtils.copyProperties(request, projectRequest);
        return projectService.getProjectPageList(projectRequest);
    }

    /**
     * 系统设置-系统-组织与项目-组织-获取组织和项目总数
     *
     * @param organizationId organizationId
     * @return java.util.Map<java.lang.String, java.lang.Long>
     */
    @GetMapping("/total")
    public Map<String, Long> getTotal(@RequestParam(value = "organizationId", required = false) String organizationId) {
        return iOrganizationService.getTotal(organizationId);
    }
}
