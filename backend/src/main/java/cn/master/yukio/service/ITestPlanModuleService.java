package cn.master.yukio.service;

import cn.master.yukio.dto.BaseTreeNode;
import cn.master.yukio.dto.NodeMoveRequest;
import cn.master.yukio.dto.plan.TestPlanModuleCreateRequest;
import cn.master.yukio.dto.plan.TestPlanModuleUpdateRequest;
import cn.master.yukio.entity.TestPlanModule;
import com.mybatisflex.core.service.IService;

import java.util.List;

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

    List<TestPlanModule> getTree(String projectId);

    List<TestPlanModule> buildTreeAndCountResource(List<TestPlanModule> traverseList, boolean haveVirtualRootNode, String virtualRootName);
}
