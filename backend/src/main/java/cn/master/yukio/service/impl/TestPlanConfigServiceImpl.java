package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.TestPlanConfig;
import cn.master.yukio.mapper.TestPlanConfigMapper;
import cn.master.yukio.service.ITestPlanConfigService;
import org.springframework.stereotype.Service;

/**
 * 测试计划配置 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class TestPlanConfigServiceImpl extends ServiceImpl<TestPlanConfigMapper, TestPlanConfig> implements ITestPlanConfigService {

}
