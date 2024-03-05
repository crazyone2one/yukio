package cn.master.yukio.service.impl;

import cn.master.yukio.entity.ProjectApplication;
import cn.master.yukio.mapper.ProjectApplicationMapper;
import cn.master.yukio.service.IProjectApplicationService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目应用 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class ProjectApplicationServiceImpl extends ServiceImpl<ProjectApplicationMapper, ProjectApplication> implements IProjectApplicationService {

    @Override
    public ProjectApplication getByType(String projectId, String type) {
        List<ProjectApplication> projectApplications = queryChain().where(ProjectApplication::getProjectId).eq(projectId).and(ProjectApplication::getType).eq(type).list();
        return CollectionUtils.isEmpty(projectApplications) ? null : projectApplications.get(0);
    }
}
