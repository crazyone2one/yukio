package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.OperationHistory;
import cn.master.yukio.mapper.OperationHistoryMapper;
import cn.master.yukio.service.IOperationHistoryService;
import org.springframework.stereotype.Service;

/**
 * 变更记录 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class OperationHistoryServiceImpl extends ServiceImpl<OperationHistoryMapper, OperationHistory> implements IOperationHistoryService {

}
