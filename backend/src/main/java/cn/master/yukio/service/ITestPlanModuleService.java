package cn.master.yukio.service;

import cn.master.yukio.dto.NodeMoveRequest;
import cn.master.yukio.dto.plan.TestPlanModuleCreateRequest;
import cn.master.yukio.dto.plan.TestPlanModuleUpdateRequest;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.TestPlanModule;

/**
 * 测试计划模块 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestPlanModuleService extends IService<TestPlanModule> {

    String add(TestPlanModuleCreateRequest request, String operator, String requestUrl, String requestMethod);

    boolean update(TestPlanModuleUpdateRequest request, String userId, String requestUrl, String requestMethod);

    void deleteModule(String deleteId, String operator, String requestUrl, String requestMethod);

    void moveNode(NodeMoveRequest request, String currentUser, String requestUrl, String requestMethod);
}
