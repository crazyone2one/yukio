package cn.master.yukio.service;

import cn.master.yukio.entity.TestPlan;

import java.util.List;

/**
 * @author Created by 11's papa on 02/29/2024
 **/
public interface TestPlanLogService {
    void saveAddLog(TestPlan module, String operator, String requestUrl, String requestMethod);

    void saveUpdateLog(TestPlan oldTestPlan, TestPlan newTestPlan, String projectId, String operator, String requestUrl, String requestMethod);

    void saveDeleteLog(TestPlan testPlan, String operator, String requestUrl, String requestMethod);

    void saveBatchDeleteLog(List<TestPlan> testPlanList, String operator, String requestUrl, String requestMethod);
}
