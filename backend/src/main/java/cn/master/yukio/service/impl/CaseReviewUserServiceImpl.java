package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.CaseReviewUser;
import cn.master.yukio.mapper.CaseReviewUserMapper;
import cn.master.yukio.service.ICaseReviewUserService;
import org.springframework.stereotype.Service;

/**
 * 评审和评审人中间表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CaseReviewUserServiceImpl extends ServiceImpl<CaseReviewUserMapper, CaseReviewUser> implements ICaseReviewUserService {

}
