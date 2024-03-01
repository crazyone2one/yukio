package cn.master.yukio.service;

import cn.master.yukio.dto.LogDTO;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.OperationLog;

import java.util.List;

/**
 * 操作日志 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IOperationLogService extends IService<OperationLog> {

    void batchAdd(List<LogDTO> logs);

    void add(LogDTO log);
}
