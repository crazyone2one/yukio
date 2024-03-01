package cn.master.yukio.service;

import cn.master.yukio.dto.plan.*;
import cn.master.yukio.entity.TestPlan;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 测试计划 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestPlanService extends IService<TestPlan> {

    String add(TestPlanCreateRequest testPlanCreateRequest, String operator, String requestUrl, String requestMethod);

    String updateTestPlan(TestPlanUpdateRequest request, String userId, String s, String name);

    void delete(String id, String operator, String requestUrl, String requestMethod);

    void batchDelete(TestPlanBatchProcessRequest request, String operator, String requestUrl, String requestMethod);

    Page<TestPlanResponse> page(TestPlanTableRequest request);
}
