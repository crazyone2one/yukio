package cn.master.yukio.service.impl;

import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.entity.OperationHistory;
import cn.master.yukio.entity.OperationLog;
import cn.master.yukio.entity.OperationLogBlob;
import cn.master.yukio.mapper.OperationHistoryMapper;
import cn.master.yukio.mapper.OperationLogBlobMapper;
import cn.master.yukio.mapper.OperationLogMapper;
import cn.master.yukio.service.IOperationLogService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {
    private final OperationLogBlobMapper logBlobMapper;
    private final OperationHistoryMapper operationHistoryMapper;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(List<LogDTO> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        logs.forEach(item -> {
            item.setContent(subStrContent(item.getContent()));
            mapper.insert(item);
            if (item.getHistory()) {
                operationHistoryMapper.insert(getHistory(item));
            }
            logBlobMapper.insert(getBlob(item));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(LogDTO log) {
        if (StringUtils.isBlank(log.getProjectId())) {
            log.setProjectId("none");
        }
        if (StringUtils.isBlank(log.getCreateUser())) {
            log.setCreateUser("admin");
        }
        log.setContent(subStrContent(log.getContent()));
        mapper.insert(log);
        if (log.getHistory()) {
            operationHistoryMapper.insert(getHistory(log));
        }
        logBlobMapper.insert(getBlob(log));
    }

    private OperationLogBlob getBlob(LogDTO log) {
        OperationLogBlob blob = new OperationLogBlob();
        blob.setId(log.getId());
        blob.setOriginalValue(log.getOriginalValue());
        blob.setModifiedValue(log.getModifiedValue());
        return blob;
    }

    private OperationHistory getHistory(LogDTO log) {
        OperationHistory history = new OperationHistory();
        BeanUtils.copyProperties(log, history);
        return history;
    }

    private String subStrContent(String content) {
        if (StringUtils.isNotBlank(content) && content.length() > 500) {
            return content.substring(0, 499);
        }
        return content;
    }
}
