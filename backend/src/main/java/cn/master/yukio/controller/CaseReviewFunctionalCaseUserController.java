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
import cn.master.yukio.entity.CaseReviewFunctionalCaseUser;
import cn.master.yukio.service.ICaseReviewFunctionalCaseUserService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 功能用例评审和评审人的中间表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/caseReviewFunctionalCaseUser")
public class CaseReviewFunctionalCaseUserController {

    @Autowired
    private ICaseReviewFunctionalCaseUserService iCaseReviewFunctionalCaseUserService;

    /**
     * 添加功能用例评审和评审人的中间表。
     *
     * @param caseReviewFunctionalCaseUser 功能用例评审和评审人的中间表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseReviewFunctionalCaseUser caseReviewFunctionalCaseUser) {
        return iCaseReviewFunctionalCaseUserService.save(caseReviewFunctionalCaseUser);
    }

    /**
     * 根据主键删除功能用例评审和评审人的中间表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCaseReviewFunctionalCaseUserService.removeById(id);
    }

    /**
     * 根据主键更新功能用例评审和评审人的中间表。
     *
     * @param caseReviewFunctionalCaseUser 功能用例评审和评审人的中间表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseReviewFunctionalCaseUser caseReviewFunctionalCaseUser) {
        return iCaseReviewFunctionalCaseUserService.updateById(caseReviewFunctionalCaseUser);
    }

    /**
     * 查询所有功能用例评审和评审人的中间表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseReviewFunctionalCaseUser> list() {
        return iCaseReviewFunctionalCaseUserService.list();
    }

    /**
     * 根据功能用例评审和评审人的中间表主键获取详细信息。
     *
     * @param id 功能用例评审和评审人的中间表主键
     * @return 功能用例评审和评审人的中间表详情
     */
    @GetMapping("getInfo/{id}")
    public CaseReviewFunctionalCaseUser getInfo(@PathVariable Serializable id) {
        return iCaseReviewFunctionalCaseUserService.getById(id);
    }

    /**
     * 分页查询功能用例评审和评审人的中间表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseReviewFunctionalCaseUser> page(Page<CaseReviewFunctionalCaseUser> page) {
        return iCaseReviewFunctionalCaseUserService.page(page);
    }

}
