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
import cn.master.yukio.entity.OperationHistory;
import cn.master.yukio.service.IOperationHistoryService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 变更记录 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/operationHistory")
public class OperationHistoryController {

    @Autowired
    private IOperationHistoryService iOperationHistoryService;

    /**
     * 添加变更记录。
     *
     * @param operationHistory 变更记录
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody OperationHistory operationHistory) {
        return iOperationHistoryService.save(operationHistory);
    }

    /**
     * 根据主键删除变更记录。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iOperationHistoryService.removeById(id);
    }

    /**
     * 根据主键更新变更记录。
     *
     * @param operationHistory 变更记录
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody OperationHistory operationHistory) {
        return iOperationHistoryService.updateById(operationHistory);
    }

    /**
     * 查询所有变更记录。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<OperationHistory> list() {
        return iOperationHistoryService.list();
    }

    /**
     * 根据变更记录主键获取详细信息。
     *
     * @param id 变更记录主键
     * @return 变更记录详情
     */
    @GetMapping("getInfo/{id}")
    public OperationHistory getInfo(@PathVariable Serializable id) {
        return iOperationHistoryService.getById(id);
    }

    /**
     * 分页查询变更记录。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<OperationHistory> page(Page<OperationHistory> page) {
        return iOperationHistoryService.page(page);
    }

}
