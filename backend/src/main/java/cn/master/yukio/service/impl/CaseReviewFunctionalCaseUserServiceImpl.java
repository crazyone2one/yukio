package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.CaseReviewFunctionalCaseUser;
import cn.master.yukio.mapper.CaseReviewFunctionalCaseUserMapper;
import cn.master.yukio.service.ICaseReviewFunctionalCaseUserService;
import org.springframework.stereotype.Service;

/**
 * 功能用例评审和评审人的中间表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CaseReviewFunctionalCaseUserServiceImpl extends ServiceImpl<CaseReviewFunctionalCaseUserMapper, CaseReviewFunctionalCaseUser> implements ICaseReviewFunctionalCaseUserService {

}
