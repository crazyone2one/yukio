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
import cn.master.yukio.entity.FunctionalCaseBlob;
import cn.master.yukio.service.IFunctionalCaseBlobService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 功能用例 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/functionalCaseBlob")
public class FunctionalCaseBlobController {

    @Autowired
    private IFunctionalCaseBlobService iFunctionalCaseBlobService;

    /**
     * 添加功能用例。
     *
     * @param functionalCaseBlob 功能用例
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody FunctionalCaseBlob functionalCaseBlob) {
        return iFunctionalCaseBlobService.save(functionalCaseBlob);
    }

    /**
     * 根据主键删除功能用例。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iFunctionalCaseBlobService.removeById(id);
    }

    /**
     * 根据主键更新功能用例。
     *
     * @param functionalCaseBlob 功能用例
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody FunctionalCaseBlob functionalCaseBlob) {
        return iFunctionalCaseBlobService.updateById(functionalCaseBlob);
    }

    /**
     * 查询所有功能用例。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<FunctionalCaseBlob> list() {
        return iFunctionalCaseBlobService.list();
    }

    /**
     * 根据功能用例主键获取详细信息。
     *
     * @param id 功能用例主键
     * @return 功能用例详情
     */
    @GetMapping("getInfo/{id}")
    public FunctionalCaseBlob getInfo(@PathVariable Serializable id) {
        return iFunctionalCaseBlobService.getById(id);
    }

    /**
     * 分页查询功能用例。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<FunctionalCaseBlob> page(Page<FunctionalCaseBlob> page) {
        return iFunctionalCaseBlobService.page(page);
    }

}
