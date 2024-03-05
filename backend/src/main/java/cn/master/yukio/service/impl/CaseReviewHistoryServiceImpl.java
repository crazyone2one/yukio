package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.CaseReviewHistory;
import cn.master.yukio.mapper.CaseReviewHistoryMapper;
import cn.master.yukio.service.ICaseReviewHistoryService;
import org.springframework.stereotype.Service;

/**
 * 评审历史表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CaseReviewHistoryServiceImpl extends ServiceImpl<CaseReviewHistoryMapper, CaseReviewHistory> implements ICaseReviewHistoryService {

}
