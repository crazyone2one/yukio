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
import cn.master.yukio.entity.CaseReviewModule;
import cn.master.yukio.service.ICaseReviewModuleService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用例评审模块 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/caseReviewModule")
public class CaseReviewModuleController {

    @Autowired
    private ICaseReviewModuleService iCaseReviewModuleService;

    /**
     * 添加用例评审模块。
     *
     * @param caseReviewModule 用例评审模块
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseReviewModule caseReviewModule) {
        return iCaseReviewModuleService.save(caseReviewModule);
    }

    /**
     * 根据主键删除用例评审模块。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCaseReviewModuleService.removeById(id);
    }

    /**
     * 根据主键更新用例评审模块。
     *
     * @param caseReviewModule 用例评审模块
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseReviewModule caseReviewModule) {
        return iCaseReviewModuleService.updateById(caseReviewModule);
    }

    /**
     * 查询所有用例评审模块。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseReviewModule> list() {
        return iCaseReviewModuleService.list();
    }

    /**
     * 根据用例评审模块主键获取详细信息。
     *
     * @param id 用例评审模块主键
     * @return 用例评审模块详情
     */
    @GetMapping("getInfo/{id}")
    public CaseReviewModule getInfo(@PathVariable Serializable id) {
        return iCaseReviewModuleService.getById(id);
    }

    /**
     * 分页查询用例评审模块。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseReviewModule> page(Page<CaseReviewModule> page) {
        return iCaseReviewModuleService.page(page);
    }

}
