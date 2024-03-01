package cn.master.yukio.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.yukio.entity.TestPlanConfig;
import cn.master.yukio.service.ITestPlanConfigService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 测试计划配置 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/testPlanConfig")
public class TestPlanConfigController {

    @Autowired
    private ITestPlanConfigService iTestPlanConfigService;

    /**
     * 添加测试计划配置。
     *
     * @param testPlanConfig 测试计划配置
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody TestPlanConfig testPlanConfig) {
        return iTestPlanConfigService.save(testPlanConfig);
    }

    /**
     * 根据主键删除测试计划配置。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iTestPlanConfigService.removeById(id);
    }

    /**
     * 根据主键更新测试计划配置。
     *
     * @param testPlanConfig 测试计划配置
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody TestPlanConfig testPlanConfig) {
        return iTestPlanConfigService.updateById(testPlanConfig);
    }

    /**
     * 查询所有测试计划配置。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestPlanConfig> list() {
        return iTestPlanConfigService.list();
    }

    /**
     * 根据测试计划配置主键获取详细信息。
     *
     * @param id 测试计划配置主键
     * @return 测试计划配置详情
     */
    @GetMapping("getInfo/{id}")
    public TestPlanConfig getInfo(@PathVariable Serializable id) {
        return iTestPlanConfigService.getById(id);
    }

    /**
     * 分页查询测试计划配置。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TestPlanConfig> page(Page<TestPlanConfig> page) {
        return iTestPlanConfigService.page(page);
    }

}
