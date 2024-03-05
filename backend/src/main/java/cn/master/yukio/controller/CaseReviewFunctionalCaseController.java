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
import cn.master.yukio.entity.CaseReviewFunctionalCase;
import cn.master.yukio.service.ICaseReviewFunctionalCaseService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用例评审和功能用例的中间表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/caseReviewFunctionalCase")
public class CaseReviewFunctionalCaseController {

    @Autowired
    private ICaseReviewFunctionalCaseService iCaseReviewFunctionalCaseService;

    /**
     * 添加用例评审和功能用例的中间表。
     *
     * @param caseReviewFunctionalCase 用例评审和功能用例的中间表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseReviewFunctionalCase caseReviewFunctionalCase) {
        return iCaseReviewFunctionalCaseService.save(caseReviewFunctionalCase);
    }

    /**
     * 根据主键删除用例评审和功能用例的中间表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCaseReviewFunctionalCaseService.removeById(id);
    }

    /**
     * 根据主键更新用例评审和功能用例的中间表。
     *
     * @param caseReviewFunctionalCase 用例评审和功能用例的中间表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseReviewFunctionalCase caseReviewFunctionalCase) {
        return iCaseReviewFunctionalCaseService.updateById(caseReviewFunctionalCase);
    }

    /**
     * 查询所有用例评审和功能用例的中间表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseReviewFunctionalCase> list() {
        return iCaseReviewFunctionalCaseService.list();
    }

    /**
     * 根据用例评审和功能用例的中间表主键获取详细信息。
     *
     * @param id 用例评审和功能用例的中间表主键
     * @return 用例评审和功能用例的中间表详情
     */
    @GetMapping("getInfo/{id}")
    public CaseReviewFunctionalCase getInfo(@PathVariable Serializable id) {
        return iCaseReviewFunctionalCaseService.getById(id);
    }

    /**
     * 分页查询用例评审和功能用例的中间表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseReviewFunctionalCase> page(Page<CaseReviewFunctionalCase> page) {
        return iCaseReviewFunctionalCaseService.page(page);
    }

}
