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
import cn.master.yukio.entity.CaseReviewUser;
import cn.master.yukio.service.ICaseReviewUserService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 评审和评审人中间表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/caseReviewUser")
public class CaseReviewUserController {

    @Autowired
    private ICaseReviewUserService iCaseReviewUserService;

    /**
     * 添加评审和评审人中间表。
     *
     * @param caseReviewUser 评审和评审人中间表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseReviewUser caseReviewUser) {
        return iCaseReviewUserService.save(caseReviewUser);
    }

    /**
     * 根据主键删除评审和评审人中间表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCaseReviewUserService.removeById(id);
    }

    /**
     * 根据主键更新评审和评审人中间表。
     *
     * @param caseReviewUser 评审和评审人中间表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseReviewUser caseReviewUser) {
        return iCaseReviewUserService.updateById(caseReviewUser);
    }

    /**
     * 查询所有评审和评审人中间表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseReviewUser> list() {
        return iCaseReviewUserService.list();
    }

    /**
     * 根据评审和评审人中间表主键获取详细信息。
     *
     * @param id 评审和评审人中间表主键
     * @return 评审和评审人中间表详情
     */
    @GetMapping("getInfo/{id}")
    public CaseReviewUser getInfo(@PathVariable Serializable id) {
        return iCaseReviewUserService.getById(id);
    }

    /**
     * 分页查询评审和评审人中间表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseReviewUser> page(Page<CaseReviewUser> page) {
        return iCaseReviewUserService.page(page);
    }

}
