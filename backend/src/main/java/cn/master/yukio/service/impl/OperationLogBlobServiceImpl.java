package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.OperationLogBlob;
import cn.master.yukio.mapper.OperationLogBlobMapper;
import cn.master.yukio.service.IOperationLogBlobService;
import org.springframework.stereotype.Service;

/**
 * 操作日志内容详情 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class OperationLogBlobServiceImpl extends ServiceImpl<OperationLogBlobMapper, OperationLogBlob> implements IOperationLogBlobService {

}
