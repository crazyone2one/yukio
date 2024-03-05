package cn.master.yukio.service.impl;

import cn.master.yukio.constants.CaseEvent;
import cn.master.yukio.constants.CaseReviewStatus;
import cn.master.yukio.constants.FunctionalCaseReviewStatus;
import cn.master.yukio.entity.*;
import cn.master.yukio.mapper.CaseReviewFunctionalCaseMapper;
import cn.master.yukio.mapper.CaseReviewFunctionalCaseUserMapper;
import cn.master.yukio.mapper.CaseReviewMapper;
import cn.master.yukio.service.BaseCaseProvider;
import cn.master.yukio.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.CaseReviewFunctionalCaseTableDef.CASE_REVIEW_FUNCTIONAL_CASE;
import static cn.master.yukio.entity.table.FunctionalCaseTableDef.FUNCTIONAL_CASE;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CaseReviewCaseProvider implements BaseCaseProvider {
    private final CaseReviewMapper caseReviewMapper;
    private final CaseReviewFunctionalCaseUserMapper caseReviewFunctionalCaseUserMapper;
    private final CaseReviewFunctionalCaseMapper caseReviewFunctionalCaseMapper;


    public static final String UN_REVIEWED_COUNT = "unReviewedCount";
    public static final String UNDER_REVIEWED_COUNT = "underReviewedCount";
    public static final String PASS_COUNT = "passCount";

    public static final String UN_PASS_COUNT = "unPassCount";

    public static final String RE_REVIEWED_COUNT = "reReviewedCount";

    @Async
    @Override
    public void updateCaseReview(Map<String, Object> paramMap) {
        String event = paramMap.get(CaseEvent.Param.EVENT_NAME).toString();
        switch (event) {
            case CaseEvent.Event.ASSOCIATE -> updateCaseReviewByAssociate(paramMap);
            case CaseEvent.Event.DISASSOCIATE -> updateCaseReviewByDisAssociate(paramMap);
            case CaseEvent.Event.DELETE_FUNCTIONAL_CASE -> updateCaseReviewByDeleteFunctionalCase(paramMap);
            case CaseEvent.Event.DELETE_TRASH_FUNCTIONAL_CASE -> updateCaseReviewByDeleteTrashFunctionalCase(paramMap);
            case CaseEvent.Event.RECOVER_FUNCTIONAL_CASE -> updateCaseReviewByRecoverFunctionalCase(paramMap);
            case CaseEvent.Event.REVIEW_FUNCTIONAL_CASE -> updateCaseReviewByReviewFunctionalCase(paramMap);
            default -> log.info("CaseProvider: " + event);
        }
    }

    private void updateCaseReviewByReviewFunctionalCase(Map<String, Object> paramMap) {
        try {
            String reviewId = paramMap.get(CaseEvent.Param.REVIEW_ID).toString();
            CaseReview caseReview = caseReviewMapper.selectOneById(reviewId);
            if (caseReview == null) {
                return;
            }
            Object caseIds = paramMap.get(CaseEvent.Param.CASE_IDS);
            List<String> caseIdList = JsonUtils.parseArray(JsonUtils.toJsonString(caseIds), String.class);
            if (CollectionUtils.isEmpty(caseIdList)) {
                return;
            }
            Object statusObjMap = paramMap.get(CaseEvent.Param.STATUS_MAP);
            Map<String, String> statusMap = JsonUtils.parseObject(JsonUtils.toJsonString(statusObjMap), new TypeReference<>() {
            });
            updateFunctionalCase(statusMap);
            List<CaseReviewFunctionalCase> caseReviewFunctionalCases = getListExcludes(List.of(reviewId), caseIdList, false);
            Map<String, Integer> caseCountMap = getCaseCountMap(caseReviewFunctionalCases);
            Object mapCount = paramMap.get(CaseEvent.Param.COUNT_MAP);
            Map<String, Integer> map = JsonUtils.parseObject(JsonUtils.toJsonString(mapCount), new TypeReference<>() {
            });
            updateMapCount(map, caseCountMap);
            updateCaseReview(reviewId, caseReviewFunctionalCases.size() + caseIdList.size(), caseCountMap, paramMap.get(CaseEvent.Param.USER_ID).toString());

        } catch (Exception e) {
            log.error(CaseEvent.Event.REVIEW_FUNCTIONAL_CASE + "事件更新失败：{}", e.getMessage());
        }
    }

    private void updateMapCount(Map<String, Integer> map, Map<String, Integer> caseCountMap) {
        if (map.get(FunctionalCaseReviewStatus.UN_REVIEWED.toString()) != null) {
            Integer i = caseCountMap.get(UN_REVIEWED_COUNT);
            caseCountMap.put(UN_REVIEWED_COUNT, map.get(FunctionalCaseReviewStatus.UN_REVIEWED.toString()) + i);
        }
        if (map.get(FunctionalCaseReviewStatus.UNDER_REVIEWED.toString()) != null) {
            Integer i = caseCountMap.get(UNDER_REVIEWED_COUNT);
            caseCountMap.put(UNDER_REVIEWED_COUNT, map.get(FunctionalCaseReviewStatus.UNDER_REVIEWED.toString()) + i);
        }
        if (map.get(FunctionalCaseReviewStatus.PASS.toString()) != null) {
            Integer i = caseCountMap.get(PASS_COUNT);
            caseCountMap.put(PASS_COUNT, map.get(FunctionalCaseReviewStatus.PASS.toString()) + i);
        }
        if (map.get(FunctionalCaseReviewStatus.UN_PASS.toString()) != null) {
            Integer i = caseCountMap.get(UN_PASS_COUNT);
            caseCountMap.put(UN_PASS_COUNT, map.get(FunctionalCaseReviewStatus.UN_PASS.toString()) + i);
        }
        if (map.get(FunctionalCaseReviewStatus.RE_REVIEWED.toString()) != null) {
            Integer i = caseCountMap.get(RE_REVIEWED_COUNT);
            caseCountMap.put(RE_REVIEWED_COUNT, map.get(FunctionalCaseReviewStatus.RE_REVIEWED.toString()) + i);
        }
    }

    private void updateFunctionalCase(Map<String, String> statusMap) {
        statusMap.forEach((castId, status) -> {
            UpdateChain.of(FunctionalCase.class)
                    .set(FunctionalCase::getReviewStatus, status)
                    .where(FunctionalCase::getId).eq(castId)
                    .update();
        });
    }

    private void updateCaseReviewByRecoverFunctionalCase(Map<String, Object> paramMap) {
        try {
            Object caseIds = paramMap.get(CaseEvent.Param.CASE_IDS);
            List<String> caseIdList = JsonUtils.parseArray(JsonUtils.toJsonString(caseIds), String.class);
            if (CollectionUtils.isEmpty(caseIdList)) {
                return;
            }
            List<CaseReviewFunctionalCase> recoverCases = QueryChain.of(CaseReviewFunctionalCase.class).where(CaseReviewFunctionalCase::getCaseId).in(caseIdList).list();
            if (CollectionUtils.isEmpty(recoverCases)) {
                return;
            }
            List<String> reviewIds = recoverCases.stream().map(CaseReviewFunctionalCase::getReviewId).distinct().toList();
            Map<String, List<CaseReviewFunctionalCase>> recoverCaseMap = recoverCases.stream().collect(Collectors.groupingBy(CaseReviewFunctionalCase::getReviewId));

            List<CaseReviewFunctionalCase> caseReviewFunctionalCases = getListExcludes(reviewIds, caseIdList, false);
            Map<String, List<CaseReviewFunctionalCase>> reviewIdMap = caseReviewFunctionalCases.stream().collect(Collectors.groupingBy(CaseReviewFunctionalCase::getReviewId));
            List<CaseReview> caseReviews = QueryChain.of(CaseReview.class).where(CaseReview::getId).in(reviewIds).list();
            Map<String, CaseReview> reviewMap = caseReviews.stream().collect(Collectors.toMap(CaseReview::getId, t -> t));
            reviewMap.forEach((reviewId, caseReview) -> {
                List<CaseReviewFunctionalCase> recoverCaseList = recoverCaseMap.get(reviewId);
                List<CaseReviewFunctionalCase> caseReviewFunctionalCaseList = reviewIdMap.get(reviewId);
                if (CollectionUtils.isNotEmpty(caseReviewFunctionalCaseList)) {
                    caseReviewFunctionalCaseList.addAll(recoverCaseList);
                } else {
                    caseReviewFunctionalCaseList = recoverCaseList;
                }
                Map<String, Integer> caseCountMap = getCaseCountMap(caseReviewFunctionalCaseList);
                updateCaseReview(reviewId, caseReviewFunctionalCaseList.size(), caseCountMap, paramMap.get(CaseEvent.Param.USER_ID).toString());
            });
        } catch (Exception e) {
            log.error(CaseEvent.Event.RECOVER_FUNCTIONAL_CASE + "事件更新失败：{}", e.getMessage());
        }
    }

    private void updateCaseReviewByDeleteTrashFunctionalCase(Map<String, Object> paramMap) {
        try {
            Object caseIds = paramMap.get(CaseEvent.Param.CASE_IDS);
            //要被删除的caseIds
            List<String> caseIdList = JsonUtils.parseArray(JsonUtils.toJsonString(caseIds), String.class);
            if (CollectionUtils.isEmpty(caseIdList)) {
                return;
            }
            if (startUpdateCaseReview(paramMap, caseIdList)) {
                return;
            }
            //删除关联关系
            QueryChain<CaseReviewFunctionalCaseUser> qw = QueryChain.of(CaseReviewFunctionalCaseUser.class).where(CaseReviewFunctionalCaseUser::getCaseId).in(caseIdList);
            caseReviewFunctionalCaseUserMapper.deleteByQuery(qw);
            //从回收站删除也删除关联关系
            QueryChain<CaseReviewFunctionalCase> caseReviewFunctionalCaseQueryChain = QueryChain.of(CaseReviewFunctionalCase.class).where(CaseReviewFunctionalCase::getCaseId).in(caseIdList);
            caseReviewFunctionalCaseMapper.deleteByQuery(caseReviewFunctionalCaseQueryChain);
        } catch (Exception e) {
            log.error(CaseEvent.Event.DELETE_TRASH_FUNCTIONAL_CASE + "事件更新失败：{}", e.getMessage());
        }
    }

    private void updateCaseReviewByDeleteFunctionalCase(Map<String, Object> paramMap) {
        try {
            Object caseIds = paramMap.get(CaseEvent.Param.CASE_IDS);
            List<String> caseIdList = JsonUtils.parseArray(JsonUtils.toJsonString(caseIds), String.class);
            if (CollectionUtils.isEmpty(caseIdList)) {
                return;
            }
            startUpdateCaseReview(paramMap, caseIdList);
        } catch (Exception e) {
            log.error(CaseEvent.Event.DELETE_FUNCTIONAL_CASE + "事件更新失败：{}", e.getMessage());
        }
    }

    private boolean startUpdateCaseReview(Map<String, Object> paramMap, List<String> caseIdList) {
        List<CaseReviewFunctionalCase> deletedCaseReviewFunctionalCases = QueryChain.of(CaseReviewFunctionalCase.class)
                .where(CaseReviewFunctionalCase::getCaseId).in(caseIdList).list();
        if (CollectionUtils.isEmpty(deletedCaseReviewFunctionalCases)) {
            return true;
        }
        List<String> reviewIds = deletedCaseReviewFunctionalCases.stream().map(CaseReviewFunctionalCase::getReviewId).distinct().toList();
        //获取与选中case无关的关联关系
        List<CaseReviewFunctionalCase> caseReviewFunctionalCases = getListExcludes(reviewIds, caseIdList, false);
        Map<String, List<CaseReviewFunctionalCase>> reviewIdMap = caseReviewFunctionalCases.stream().collect(Collectors.groupingBy(CaseReviewFunctionalCase::getReviewId));
        reviewIdMap.forEach((reviewId, caseReviewFunctionalCaseList) -> {
            Map<String, Integer> caseCountMap = getCaseCountMap(caseReviewFunctionalCaseList);
            updateCaseReview(reviewId, caseReviewFunctionalCaseList.size(), caseCountMap, paramMap.get(CaseEvent.Param.USER_ID).toString());
        });
        return false;
    }

    private void updateCaseReviewByDisAssociate(Map<String, Object> paramMap) {
        try {
            String reviewId = paramMap.get(CaseEvent.Param.REVIEW_ID).toString();
            CaseReview caseReview = caseReviewMapper.selectOneById(reviewId);
            if (caseReview == null) {
                return;
            }
            Object caseIds = paramMap.get(CaseEvent.Param.CASE_IDS);
            List<String> caseIdList = JsonUtils.parseArray(JsonUtils.toJsonString(caseIds), String.class);
            //获取与选中case无关的其他case
            List<CaseReviewFunctionalCase> caseReviewFunctionalCases = getListExcludes(List.of(reviewId), caseIdList, false);
            int caseCount = caseReviewFunctionalCases.size();
            Map<String, Integer> caseCountMap = getCaseCountMap(caseReviewFunctionalCases);
            updateCaseReview(reviewId, caseCount, caseCountMap, paramMap.get(CaseEvent.Param.USER_ID).toString());
            //删除用例和用例评审人的关系
            deleteCaseReviewFunctionalCaseUser(paramMap);
            //将评审历史状态置为true
            //caseReviewHistoryService.updateDelete(caseIdList, reviewId, true);
            UpdateChain.of(CaseReviewHistory.class)
                    .set(CaseReviewHistory::getDeleted, true)
                    .where(CaseReviewHistory::getCaseId).in(caseIdList)
                    .and(CaseReviewHistory::getReviewId).eq(reviewId).update();
            //检查更新用例的评审状态。如果用例没有任何评审关联，就置为未评审, 否则置为更新建时间最晚的那个
            updateCaseStatus(caseIdList);
        } catch (Exception e) {
            log.error(CaseEvent.Event.DISASSOCIATE + "事件更新失败：{}", e.getMessage());
        }
    }

    private void updateCaseStatus(List<String> caseIdList) {
        List<CaseReviewFunctionalCase> otherReviewFunctionalCases = QueryChain.of(CaseReviewFunctionalCase.class)
                .where(CaseReviewFunctionalCase::getCaseId).in(caseIdList)
                .orderBy(CaseReviewFunctionalCase::getUpdateTime).desc().list();
        if (CollectionUtils.isEmpty(otherReviewFunctionalCases)) {
            for (String id : caseIdList) {
                UpdateChain.of(FunctionalCase.class)
                        .set(FunctionalCase::getReviewStatus, FunctionalCaseReviewStatus.UN_REVIEWED.toString())
                        .where(FunctionalCase::getId).eq(id).update();
            }
        } else {
            Map<String, List<CaseReviewFunctionalCase>> collect = otherReviewFunctionalCases.stream().collect(Collectors.groupingBy(CaseReviewFunctionalCase::getCaseId));
            collect.forEach((caseId, caseList) -> {
                UpdateChain.of(FunctionalCase.class)
                        .set(FunctionalCase::getReviewStatus, caseList.get(0).getStatus())
                        .where(FunctionalCase::getId).eq(caseId).update();
            });
        }
    }

    private void deleteCaseReviewFunctionalCaseUser(Map<String, Object> paramMap) {
        Object caseIds = paramMap.get(CaseEvent.Param.CASE_IDS);
        List<String> caseIdList = JsonUtils.parseArray(JsonUtils.toJsonString(caseIds), String.class);
        String reviewId = paramMap.get(CaseEvent.Param.REVIEW_ID).toString();
        QueryChain<CaseReviewFunctionalCaseUser> qw = QueryChain.of(CaseReviewFunctionalCaseUser.class)
                .where(CaseReviewFunctionalCaseUser::getCaseId).in(caseIdList)
                .and(CaseReviewFunctionalCaseUser::getReviewId).eq(reviewId);
        caseReviewFunctionalCaseUserMapper.deleteByQuery(qw);
    }

    private void updateCaseReviewByAssociate(Map<String, Object> paramMap) {
        try {
            String reviewId = paramMap.get(CaseEvent.Param.REVIEW_ID).toString();
            Object caseIds = paramMap.get(CaseEvent.Param.CASE_IDS);
            List<String> caseIdList = JsonUtils.parseArray(JsonUtils.toJsonString(caseIds), String.class);
            //获取关联前的caseIds
            List<CaseReviewFunctionalCase> beforeCaseReviewFunctionalCases = getListExcludes(List.of(reviewId), caseIdList, false);
            int caseCount = caseIdList.size() + beforeCaseReviewFunctionalCases.size();
            Map<String, Integer> mapCount = getCaseCountMap(beforeCaseReviewFunctionalCases);
            //新关联的都是未评审的，这里加上
            Integer i = mapCount.get(UN_REVIEWED_COUNT);
            mapCount.put(UN_REVIEWED_COUNT, i + caseIdList.size());
            updateCaseReview(reviewId, caseCount, mapCount, paramMap.get(CaseEvent.Param.USER_ID).toString());
        } catch (Exception e) {
            log.error(CaseEvent.Event.ASSOCIATE + "事件更新失败：{}", e.getMessage());
        }
    }

    private void updateCaseReview(String reviewId, int caseCount, Map<String, Integer> mapCount, String userId) {
        CaseReview caseReview = new CaseReview();
        caseReview.setId(reviewId);
        //更新用例数量
        caseReview.setCaseCount(caseCount);
        //通过率
        BigDecimal passCount = BigDecimal.valueOf(mapCount.get(PASS_COUNT));
        BigDecimal totalCount = BigDecimal.valueOf(caseReview.getCaseCount());
        if (totalCount.compareTo(BigDecimal.ZERO) == 0) {
            caseReview.setPassRate(BigDecimal.ZERO);
        } else {
            BigDecimal passRate = passCount.divide(totalCount, 2, RoundingMode.HALF_UP);
            caseReview.setPassRate(passRate.multiply(BigDecimal.valueOf(100)));
        }
        boolean completed = false;
        //1.如果都是未评审，则用例评审状态为未开始
        //2.如果都是已完成状态（通过/不通过），则用例评审状态为已完成
        //3.如果有未评审/重新提审状态/评审中数量>1，则是评审中
        Integer unPassCount = mapCount.get(UN_PASS_COUNT);
        Integer unReviewedCount = mapCount.get(UN_REVIEWED_COUNT);
        Integer underReviewedCount = mapCount.get(UNDER_REVIEWED_COUNT);
        Integer reReviewedCount = mapCount.get(RE_REVIEWED_COUNT);
        if (Objects.equals(unReviewedCount, caseReview.getCaseCount())) {
            caseReview.setStatus(CaseReviewStatus.PREPARED.toString());
        } else if ((unReviewedCount + underReviewedCount + reReviewedCount) > 0) {
            caseReview.setStatus(CaseReviewStatus.UNDERWAY.toString());
        } else if ((mapCount.get(PASS_COUNT) + unPassCount) == caseReview.getCaseCount()) {
            caseReview.setStatus(CaseReviewStatus.COMPLETED.toString());
            completed = true;
        }
        caseReviewMapper.update(caseReview);
        if (completed) {
            // todo 发送通知
            //reviewSendNoticeService.sendNotice(new ArrayList<>(), userId, reviewId, NoticeConstants.TaskType.CASE_REVIEW_TASK, NoticeConstants.Event.REVIEW_COMPLETED);
        }
    }

    private Map<String, Integer> getCaseCountMap(List<CaseReviewFunctionalCase> caseReviewFunctionalCases) {
        Map<String, Integer> mapCount = new HashMap<>();
        Map<String, List<CaseReviewFunctionalCase>> caseMap = caseReviewFunctionalCases.stream().collect(Collectors.groupingBy(CaseReviewFunctionalCase::getStatus));
        if (caseMap.get(FunctionalCaseReviewStatus.UN_REVIEWED.toString()) != null) {
            mapCount.put(UN_REVIEWED_COUNT, caseMap.get(FunctionalCaseReviewStatus.UN_REVIEWED.toString()).size());
        } else {
            mapCount.put(UN_REVIEWED_COUNT, 0);
        }

        if (caseMap.get(FunctionalCaseReviewStatus.UNDER_REVIEWED.toString()) != null) {
            mapCount.put(UNDER_REVIEWED_COUNT, caseMap.get(FunctionalCaseReviewStatus.UNDER_REVIEWED.toString()).size());
        } else {
            mapCount.put(UNDER_REVIEWED_COUNT, 0);
        }

        if (caseMap.get(FunctionalCaseReviewStatus.PASS.toString()) != null) {
            mapCount.put(PASS_COUNT, caseMap.get(FunctionalCaseReviewStatus.PASS.toString()).size());
        } else {
            mapCount.put(PASS_COUNT, 0);
        }

        if (caseMap.get(FunctionalCaseReviewStatus.UN_PASS.toString()) != null) {
            mapCount.put(UN_PASS_COUNT, caseMap.get(FunctionalCaseReviewStatus.UN_PASS.toString()).size());
        } else {
            mapCount.put(UN_PASS_COUNT, 0);
        }

        if (caseMap.get(FunctionalCaseReviewStatus.RE_REVIEWED.toString()) != null) {
            mapCount.put(RE_REVIEWED_COUNT, caseMap.get(FunctionalCaseReviewStatus.RE_REVIEWED.toString()).size());
        } else {
            mapCount.put(RE_REVIEWED_COUNT, 0);
        }

        return mapCount;
    }

    private List<CaseReviewFunctionalCase> getListExcludes(List<String> reviewId, List<String> caseIdList, boolean b) {
        return QueryChain.of(CaseReviewFunctionalCase.class)
                .select(CASE_REVIEW_FUNCTIONAL_CASE.ID, CASE_REVIEW_FUNCTIONAL_CASE.REVIEW_ID, CASE_REVIEW_FUNCTIONAL_CASE.CASE_ID,
                        CASE_REVIEW_FUNCTIONAL_CASE.STATUS, CASE_REVIEW_FUNCTIONAL_CASE.CREATE_TIME, CASE_REVIEW_FUNCTIONAL_CASE.CREATE_USER)
                .from(CASE_REVIEW_FUNCTIONAL_CASE)
                .leftJoin(FUNCTIONAL_CASE).on(FUNCTIONAL_CASE.ID.eq(CASE_REVIEW_FUNCTIONAL_CASE.CASE_ID))
                .where(FUNCTIONAL_CASE.DELETED.eq(b))
                .and(CASE_REVIEW_FUNCTIONAL_CASE.REVIEW_ID.in(reviewId))
                .and(FUNCTIONAL_CASE.ID.notIn(caseIdList)).list();
    }
}
