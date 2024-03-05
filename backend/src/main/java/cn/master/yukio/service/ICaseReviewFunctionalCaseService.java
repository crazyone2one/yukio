package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.CaseReviewFunctionalCase;

/**
 * 用例评审和功能用例的中间表 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICaseReviewFunctionalCaseService extends IService<CaseReviewFunctionalCase> {
    Long getCaseFunctionalCaseNextPos(String caseReviewId);

    void addCaseReviewFunctionalCase(String caseId, String userId, String reviewId);
}
