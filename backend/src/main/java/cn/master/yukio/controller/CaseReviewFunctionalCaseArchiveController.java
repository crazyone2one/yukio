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
import cn.master.yukio.entity.CaseReviewFunctionalCaseArchive;
import cn.master.yukio.service.ICaseReviewFunctionalCaseArchiveService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用例评审归档表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/caseReviewFunctionalCaseArchive")
public class CaseReviewFunctionalCaseArchiveController {

    @Autowired
    private ICaseReviewFunctionalCaseArchiveService iCaseReviewFunctionalCaseArchiveService;

    /**
     * 添加用例评审归档表。
     *
     * @param caseReviewFunctionalCaseArchive 用例评审归档表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseReviewFunctionalCaseArchive caseReviewFunctionalCaseArchive) {
        return iCaseReviewFunctionalCaseArchiveService.save(caseReviewFunctionalCaseArchive);
    }

    /**
     * 根据主键删除用例评审归档表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCaseReviewFunctionalCaseArchiveService.removeById(id);
    }

    /**
     * 根据主键更新用例评审归档表。
     *
     * @param caseReviewFunctionalCaseArchive 用例评审归档表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseReviewFunctionalCaseArchive caseReviewFunctionalCaseArchive) {
        return iCaseReviewFunctionalCaseArchiveService.updateById(caseReviewFunctionalCaseArchive);
    }

    /**
     * 查询所有用例评审归档表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseReviewFunctionalCaseArchive> list() {
        return iCaseReviewFunctionalCaseArchiveService.list();
    }

    /**
     * 根据用例评审归档表主键获取详细信息。
     *
     * @param id 用例评审归档表主键
     * @return 用例评审归档表详情
     */
    @GetMapping("getInfo/{id}")
    public CaseReviewFunctionalCaseArchive getInfo(@PathVariable Serializable id) {
        return iCaseReviewFunctionalCaseArchiveService.getById(id);
    }

    /**
     * 分页查询用例评审归档表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseReviewFunctionalCaseArchive> page(Page<CaseReviewFunctionalCaseArchive> page) {
        return iCaseReviewFunctionalCaseArchiveService.page(page);
    }

}
