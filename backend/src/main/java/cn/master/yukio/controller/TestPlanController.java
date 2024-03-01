package cn.master.yukio.controller;

import cn.master.yukio.constants.HttpMethodConstants;
import cn.master.yukio.constants.TestPlanResourceConfig;
import cn.master.yukio.dto.plan.*;
import cn.master.yukio.entity.TestPlan;
import cn.master.yukio.service.ITestPlanService;
import cn.master.yukio.service.TestPlanManagementService;
import cn.master.yukio.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 测试计划 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test-plan")
@RequiredArgsConstructor
public class TestPlanController {

    private final ITestPlanService iTestPlanService;
    private final TestPlanManagementService testPlanManagementService;

    /**
     * 添加测试计划。
     *
     * @param request 测试计划
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public String save(@Validated @RequestBody TestPlanCreateRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getProjectId(), TestPlanResourceConfig.CHECK_TYPE_PROJECT, Collections.singletonList(TestPlanResourceConfig.CONFIG_TEST_PLAN));
        return iTestPlanService.add(request, SessionUtils.getUserId(), "/test-plan/save", HttpMethodConstants.POST.name());
    }

    /**
     * 根据主键删除测试计划。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @GetMapping("remove/{id}")
    public void remove(@NotBlank @PathVariable String id) {
        testPlanManagementService.checkModuleIsOpen(id, TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN, Collections.singletonList(TestPlanResourceConfig.CONFIG_TEST_PLAN));
        iTestPlanService.delete(id, SessionUtils.getUserId(), "/test-plan/delete", HttpMethodConstants.GET.name());
    }

    @PostMapping(value = "/batch-delete")
    public void delete(@Validated @RequestBody TestPlanBatchProcessRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getProjectId(), TestPlanResourceConfig.CHECK_TYPE_PROJECT, Collections.singletonList(TestPlanResourceConfig.CONFIG_TEST_PLAN));
        iTestPlanService.batchDelete(request, SessionUtils.getUserId(), "/test-plan/batch-delete", HttpMethodConstants.POST.name());
    }

    /**
     * 根据主键更新测试计划。
     *
     * @param request 测试计划
     */
    @PostMapping("update")
    public String update(@Validated @RequestBody TestPlanUpdateRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getId(), TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN, Collections.singletonList(TestPlanResourceConfig.CONFIG_TEST_PLAN));
        return iTestPlanService.updateTestPlan(request, SessionUtils.getUserId(), "/test-plan/update", HttpMethodConstants.POST.name());
    }

    /**
     * 查询所有测试计划。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestPlan> list() {
        return iTestPlanService.list();
    }

    /**
     * 根据测试计划主键获取详细信息。
     *
     * @param id 测试计划主键
     * @return 测试计划详情
     */
    @GetMapping("getInfo/{id}")
    public TestPlan getInfo(@PathVariable Serializable id) {
        return iTestPlanService.getById(id);
    }

    /**
     * 分页查询测试计划。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TestPlanResponse> page(@Validated @RequestBody TestPlanTableRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getProjectId(), TestPlanResourceConfig.CHECK_TYPE_PROJECT, Collections.singletonList(TestPlanResourceConfig.CONFIG_TEST_PLAN));
        return iTestPlanService.page(request);
    }

}
