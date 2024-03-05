package cn.master.yukio.service.impl;

import cn.master.yukio.constants.CaseEvent;
import cn.master.yukio.constants.FunctionalCaseReviewStatus;
import cn.master.yukio.entity.CaseReviewFunctionalCaseUser;
import cn.master.yukio.entity.CaseReviewUser;
import cn.master.yukio.mapper.CaseReviewFunctionalCaseUserMapper;
import cn.master.yukio.mapper.CaseReviewUserMapper;
import cn.master.yukio.service.BaseCaseProvider;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.CaseReviewFunctionalCase;
import cn.master.yukio.mapper.CaseReviewFunctionalCaseMapper;
import cn.master.yukio.service.ICaseReviewFunctionalCaseService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.master.yukio.entity.table.CaseReviewFunctionalCaseTableDef.CASE_REVIEW_FUNCTIONAL_CASE;
import static cn.master.yukio.util.ServiceUtils.POS_STEP;

/**
 * 用例评审和功能用例的中间表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CaseReviewFunctionalCaseServiceImpl extends ServiceImpl<CaseReviewFunctionalCaseMapper, CaseReviewFunctionalCase> implements ICaseReviewFunctionalCaseService {
    private final CaseReviewFunctionalCaseUserMapper caseReviewFunctionalCaseUserMapper;
    private final BaseCaseProvider provider;

    @Override
    public Long getCaseFunctionalCaseNextPos(String caseReviewId) {
        Long pos = queryChain().select(CASE_REVIEW_FUNCTIONAL_CASE.POS)
                .where(CASE_REVIEW_FUNCTIONAL_CASE.REVIEW_ID.eq(caseReviewId))
                .orderBy(CASE_REVIEW_FUNCTIONAL_CASE.POS.desc()).limit(1).oneAs(Long.class);
        return (pos == null ? 0 : pos) + POS_STEP;
    }

    @Override
    public void addCaseReviewFunctionalCase(String caseId, String userId, String reviewId) {
        CaseReviewFunctionalCase build = CaseReviewFunctionalCase.builder()
                .caseId(caseId)
                .reviewId(reviewId)
                .status(FunctionalCaseReviewStatus.UN_REVIEWED.toString())
                .createUser(userId)
                .pos(getCaseFunctionalCaseNextPos((reviewId)))
                .build();
        mapper.insert(build);
        // //评审人
        List<CaseReviewUser> caseReviewUsers = QueryChain.of(CaseReviewUser.class).where(CaseReviewUser::getReviewId).eq(reviewId).list();
        if (CollectionUtils.isNotEmpty(caseReviewUsers)) {
            List<CaseReviewFunctionalCaseUser> list = new ArrayList<>();
            caseReviewUsers.forEach(item->{
                CaseReviewFunctionalCaseUser caseUser = CaseReviewFunctionalCaseUser.builder()
                        .caseId(caseId)
                        .reviewId(reviewId)
                        .userId(item.getUserId())
                        .build();
                list.add(caseUser);
            });
            caseReviewFunctionalCaseUserMapper.insertBatch(list);
            //更新评审的整体状态
            Map<String, Integer> countMap = new HashMap<>();
            countMap.put(build.getStatus(), 1);
            Map<String, String> statusMap = new HashMap<>();
            statusMap.put(caseId, build.getStatus());
            Map<String, Object> param = new HashMap<>();
            param.put(CaseEvent.Param.REVIEW_ID, reviewId);
            param.put(CaseEvent.Param.USER_ID, userId);
            param.put(CaseEvent.Param.CASE_IDS, List.of(caseId));
            param.put(CaseEvent.Param.COUNT_MAP, countMap);
            param.put(CaseEvent.Param.STATUS_MAP, statusMap);
            param.put(CaseEvent.Param.EVENT_NAME, CaseEvent.Event.REVIEW_FUNCTIONAL_CASE);
            provider.updateCaseReview(param);
        }
    }
}
