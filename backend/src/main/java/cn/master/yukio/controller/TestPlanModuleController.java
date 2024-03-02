package cn.master.yukio.controller;

import cn.master.yukio.constants.HttpMethodConstants;
import cn.master.yukio.constants.TestPlanResourceConfig;
import cn.master.yukio.dto.NodeMoveRequest;
import cn.master.yukio.dto.plan.TestPlanModuleCreateRequest;
import cn.master.yukio.dto.plan.TestPlanModuleUpdateRequest;
import cn.master.yukio.service.TestPlanManagementService;
import cn.master.yukio.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.yukio.entity.TestPlanModule;
import cn.master.yukio.service.ITestPlanModuleService;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 测试计划模块 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test-plan/module")
@RequiredArgsConstructor
public class TestPlanModuleController {

    private final ITestPlanModuleService iTestPlanModuleService;
    private final TestPlanManagementService testPlanManagementService;

    /**
     * 添加测试计划模块。
     *
     * @param request 测试计划模块
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public String save(@RequestBody @Validated TestPlanModuleCreateRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getProjectId(), TestPlanResourceConfig.CHECK_TYPE_PROJECT, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        return iTestPlanModuleService.add(request, SessionUtils.getUserId(), "/test-plan/module/add", HttpMethodConstants.POST.name());
    }

    /**
     * 根据主键删除测试计划模块。
     *
     * @param id 主键
     */
    @GetMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        testPlanManagementService.checkModuleIsOpen(id, TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN_MODULE, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        iTestPlanModuleService.deleteModule(id, SessionUtils.getUserId(), "/test-plan/module/delete", HttpMethodConstants.GET.name());
    }

    /**
     * 根据主键更新测试计划模块。
     *
     * @param request 测试计划模块
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public boolean update(@RequestBody @Validated TestPlanModuleUpdateRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getId(), TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN_MODULE, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        return iTestPlanModuleService.update(request, SessionUtils.getUserId(), "/test-plan/module/update", HttpMethodConstants.POST.name());
    }

    /**
     * 测试计划管理-模块树-移动模块
     *
     * @param request
     */
    @PostMapping("/move")
    public void moveNode(@Validated @RequestBody NodeMoveRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getDragNodeId(), TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN_MODULE, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        iTestPlanModuleService.moveNode(request, SessionUtils.getUserId(), "/test-plan/module/move", HttpMethodConstants.POST.name());
    }

    /**
     * 查询所有测试计划模块。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestPlanModule> list() {
        return iTestPlanModuleService.list();
    }

    /**
     * 根据测试计划模块主键获取详细信息。
     *
     * @param id 测试计划模块主键
     * @return 测试计划模块详情
     */
    @GetMapping("getInfo/{id}")
    public TestPlanModule getInfo(@PathVariable Serializable id) {
        return iTestPlanModuleService.getById(id);
    }

    /**
     * 分页查询测试计划模块。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TestPlanModule> page(Page<TestPlanModule> page) {
        return iTestPlanModuleService.page(page);
    }

    /**
     * 测试计划管理-模块树-查找模块
     *
     * @param projectId
     * @return java.util.List<cn.master.yukio.entity.TestPlanModule>
     */
    @GetMapping("/tree/{projectId}")
    public List<TestPlanModule> getTree(@PathVariable String projectId) {
        testPlanManagementService.checkModuleIsOpen(projectId, TestPlanResourceConfig.CHECK_TYPE_PROJECT, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        return iTestPlanModuleService.getTree(projectId);
    }
}
