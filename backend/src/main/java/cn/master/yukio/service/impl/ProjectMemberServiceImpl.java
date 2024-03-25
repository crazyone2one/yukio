package cn.master.yukio.service.impl;

import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.User;
import cn.master.yukio.entity.UserRoleRelation;
import cn.master.yukio.mapper.ProjectMapper;
import cn.master.yukio.service.ProjectMemberService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.master.yukio.entity.table.UserTableDef.USER;
import static cn.master.yukio.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * @author Created by 11's papa on 03/23/2024
 **/
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {
    private final ProjectMapper projectMapper;

    @Override
    public List<UserExtendDTO> getMemberOption(String projectId, String keyword) {
        Project project = projectMapper.selectOneById(projectId);
        if (project == null) {
            return new ArrayList<>();
        }
        // 组织成员
        List<UserExtendDTO> orgMembers = QueryChain.of(User.class).select(QueryMethods.distinct(USER.ALL_COLUMNS))
                .from(UserRoleRelation.class).join(User.class).on(USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getOrganizationId())
                        .and(USER.NAME.like(keyword).or(USER.EMAIL.like(keyword))))
                .orderBy(USER.NAME.desc()).limit(100)
                .listAs(UserExtendDTO.class);
        if (CollectionUtils.isEmpty(orgMembers)) {
            return new ArrayList<>();
        }
        // 设置是否是项目成员
        List<String> orgMemberIds = orgMembers.stream().map(UserExtendDTO::getId).toList();
        List<UserRoleRelation> projectRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.in(orgMemberIds)
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId))
                .and(USER_ROLE_RELATION.ORGANIZATION_ID.eq(project.getOrganizationId()))).list();
        if (CollectionUtils.isEmpty(projectRelations)) {
            orgMembers.forEach(orgMember -> orgMember.setMemberFlag(false));
        } else {
            List<String> projectUsers = projectRelations.stream().map(UserRoleRelation::getUserId).distinct().toList();
            // 已经是项目成员的组织成员, 禁用
            orgMembers.forEach(orgMember -> orgMember.setMemberFlag(projectUsers.contains(orgMember.getId())));
        }
        return orgMembers;
    }
}
