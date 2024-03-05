package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.ProjectVersion;
import cn.master.yukio.mapper.ProjectVersionMapper;
import cn.master.yukio.service.IProjectVersionService;
import org.springframework.stereotype.Service;

/**
 * 版本管理 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class ProjectVersionServiceImpl extends ServiceImpl<ProjectVersionMapper, ProjectVersion> implements IProjectVersionService {

}
