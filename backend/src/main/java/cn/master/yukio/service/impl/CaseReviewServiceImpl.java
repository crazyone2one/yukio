package cn.master.yukio.service.impl;

import cn.master.yukio.exception.MSException;
import cn.master.yukio.handler.result.CaseManagementResultCode;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.CaseReview;
import cn.master.yukio.mapper.CaseReviewMapper;
import cn.master.yukio.service.ICaseReviewService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 用例评审 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CaseReviewServiceImpl extends ServiceImpl<CaseReviewMapper, CaseReview> implements ICaseReviewService {

    @Override
    public CaseReview checkCaseReview(String caseReviewId) {
        CaseReview caseReview = mapper.selectOneById(caseReviewId);
        if (Objects.isNull(caseReview)) {
            throw new MSException(CaseManagementResultCode.CASE_REVIEW_NOT_FOUND);
        }
        return caseReview;
    }
}
