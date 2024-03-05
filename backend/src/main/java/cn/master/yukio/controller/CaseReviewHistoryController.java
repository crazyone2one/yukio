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
import cn.master.yukio.entity.CaseReviewHistory;
import cn.master.yukio.service.ICaseReviewHistoryService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 评审历史表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/caseReviewHistory")
public class CaseReviewHistoryController {

    @Autowired
    private ICaseReviewHistoryService iCaseReviewHistoryService;

    /**
     * 添加评审历史表。
     *
     * @param caseReviewHistory 评审历史表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseReviewHistory caseReviewHistory) {
        return iCaseReviewHistoryService.save(caseReviewHistory);
    }

    /**
     * 根据主键删除评审历史表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCaseReviewHistoryService.removeById(id);
    }

    /**
     * 根据主键更新评审历史表。
     *
     * @param caseReviewHistory 评审历史表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseReviewHistory caseReviewHistory) {
        return iCaseReviewHistoryService.updateById(caseReviewHistory);
    }

    /**
     * 查询所有评审历史表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseReviewHistory> list() {
        return iCaseReviewHistoryService.list();
    }

    /**
     * 根据评审历史表主键获取详细信息。
     *
     * @param id 评审历史表主键
     * @return 评审历史表详情
     */
    @GetMapping("getInfo/{id}")
    public CaseReviewHistory getInfo(@PathVariable Serializable id) {
        return iCaseReviewHistoryService.getById(id);
    }

    /**
     * 分页查询评审历史表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseReviewHistory> page(Page<CaseReviewHistory> page) {
        return iCaseReviewHistoryService.page(page);
    }

}
