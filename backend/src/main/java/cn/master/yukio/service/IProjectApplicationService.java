package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.ProjectApplication;

/**
 * 项目应用 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IProjectApplicationService extends IService<ProjectApplication> {
    ProjectApplication getByType(String projectId, String type);

}
