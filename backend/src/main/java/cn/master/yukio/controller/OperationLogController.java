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
import cn.master.yukio.entity.OperationLog;
import cn.master.yukio.service.IOperationLogService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 操作日志 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/operationLog")
public class OperationLogController {

    @Autowired
    private IOperationLogService iOperationLogService;

    /**
     * 添加操作日志。
     *
     * @param operationLog 操作日志
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody OperationLog operationLog) {
        return iOperationLogService.save(operationLog);
    }

    /**
     * 根据主键删除操作日志。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iOperationLogService.removeById(id);
    }

    /**
     * 根据主键更新操作日志。
     *
     * @param operationLog 操作日志
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody OperationLog operationLog) {
        return iOperationLogService.updateById(operationLog);
    }

    /**
     * 查询所有操作日志。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<OperationLog> list() {
        return iOperationLogService.list();
    }

    /**
     * 根据操作日志主键获取详细信息。
     *
     * @param id 操作日志主键
     * @return 操作日志详情
     */
    @GetMapping("getInfo/{id}")
    public OperationLog getInfo(@PathVariable Serializable id) {
        return iOperationLogService.getById(id);
    }

    /**
     * 分页查询操作日志。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<OperationLog> page(Page<OperationLog> page) {
        return iOperationLogService.page(page);
    }

}
