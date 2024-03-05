package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.CaseReview;

/**
 * 用例评审 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICaseReviewService extends IService<CaseReview> {

    CaseReview checkCaseReview(String reviewId);
}
