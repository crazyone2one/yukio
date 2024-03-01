package cn.master.yukio.service.impl;

import cn.master.yukio.constants.ApplicationNumScope;
import cn.master.yukio.constants.ModuleConstants;
import cn.master.yukio.constants.TestPlanConstants;
import cn.master.yukio.dto.plan.*;
import cn.master.yukio.entity.TestPlanConfig;
import cn.master.yukio.entity.TestPlanModule;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.TestPlanConfigMapper;
import cn.master.yukio.mapper.TestPlanModuleMapper;
import cn.master.yukio.service.TestPlanLogService;
import cn.master.yukio.util.BatchProcessUtils;
import cn.master.yukio.util.NumGenerator;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.TestPlan;
import cn.master.yukio.mapper.TestPlanMapper;
import cn.master.yukio.service.ITestPlanService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.TestPlanTableDef.TEST_PLAN;
import static cn.master.yukio.entity.table.UserTableDef.USER;

/**
 * 测试计划 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements ITestPlanService {
    private final TestPlanModuleMapper testPlanModuleMapper;
    private final TestPlanConfigMapper testPlanConfigMapper;
    private final TestPlanLogService testPlanLogService;
    private static final long MAX_TEST_PLAN_SIZE = 999;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(TestPlanCreateRequest testPlanCreateRequest, String operator, String requestUrl, String requestMethod) {
        checkModule(testPlanCreateRequest.getModuleId());
        TestPlan createTestPlan = new TestPlan();
        BeanUtils.copyProperties(testPlanCreateRequest, createTestPlan);
        validateTestPlan(createTestPlan);
        createTestPlan.setNum(NumGenerator.nextNum(testPlanCreateRequest.getProjectId(), ApplicationNumScope.TEST_PLAN));
        createTestPlan.setCreateUser(operator);
        createTestPlan.setUpdateUser(operator);
        createTestPlan.setStatus(TestPlanConstants.TEST_PLAN_STATUS_PREPARED);
        mapper.insert(createTestPlan);
        TestPlanConfig build = TestPlanConfig.builder()
                .testPlanId(createTestPlan.getId())
                .automaticStatusUpdate(testPlanCreateRequest.isAutomaticStatusUpdate())
                .repeatCase(testPlanCreateRequest.isRepeatCase())
                .build();
        testPlanConfigMapper.insert(build);
        testPlanLogService.saveAddLog(createTestPlan, operator, requestUrl, requestMethod);
        return createTestPlan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateTestPlan(TestPlanUpdateRequest request, String userId, String requestUrl, String requestMethod) {
        TestPlan testPlan = mapper.selectOneById(request.getId());
        if (!ObjectUtils.allNull(request.getName(), request.getModuleId(), request.getTags(), request.getPlannedEndTime(), request.getPlannedStartTime(), request.getDescription(), request.getTestPlanGroupId())) {
            TestPlan updateTestPlan = new TestPlan();
            updateTestPlan.setId(request.getId());
            if (StringUtils.isNotBlank(request.getName())) {
                updateTestPlan.setName(request.getName());
                updateTestPlan.setProjectId(testPlan.getProjectId());
                validateTestPlan(updateTestPlan);
            }
            if (StringUtils.isNotBlank(request.getModuleId())) {
                //检查模块的合法性
                this.checkModule(request.getModuleId());
                updateTestPlan.setModuleId(request.getModuleId());
            }
            if (CollectionUtils.isNotEmpty(request.getTags())) {
                updateTestPlan.setTags(request.getTags());
            }
            updateTestPlan.setPlannedStartTime(request.getPlannedStartTime());
            updateTestPlan.setPlannedEndTime(request.getPlannedEndTime());
            updateTestPlan.setDescription(request.getDescription());
            updateTestPlan.setGroupId(request.getTestPlanGroupId());
            updateTestPlan.setType(testPlan.getType());
            mapper.update(updateTestPlan);
            if (!ObjectUtils.allNull(request.getAutomaticStatusUpdate(), request.getRepeatCase(), request.getPassThreshold())) {
                TestPlanConfig build = TestPlanConfig.builder()
                        .testPlanId(testPlan.getId())
                        .automaticStatusUpdate(request.getAutomaticStatusUpdate())
                        .repeatCase(request.getRepeatCase())
                        .passThreshold(request.getPassThreshold())
                        .build();
                testPlanConfigMapper.update(build);
            }
            testPlanLogService.saveUpdateLog(testPlan, mapper.selectOneById(request.getId()), testPlan.getProjectId(), userId, requestUrl, requestMethod);
        }
        return request.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id, String operator, String requestUrl, String requestMethod) {
        TestPlan testPlan = mapper.selectOneById(id);
        if (StringUtils.equals(testPlan.getType(), TestPlanConstants.TEST_PLAN_TYPE_GROUP)) {
            deleteGroupByList(Collections.singletonList(testPlan.getId()));
        } else {
            mapper.deleteById(id);
            cascadeDeleteTestPlanIds(Collections.singletonList(id));
        }
        testPlanLogService.saveDeleteLog(testPlan, operator, requestUrl, requestMethod);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(TestPlanBatchProcessRequest request, String operator, String requestUrl, String requestMethod) {
        List<String> deleteIdList = request.getSelectIds();
        if (request.isSelectAll()) {
            TestPlanQueryConditions conditions = new TestPlanQueryConditions(request.getModuleIds(), request.getProjectId(), request.getCondition());
            conditions.setHiddenIds(request.getExcludeIds());
            queryChain().select(TEST_PLAN.ID)
                    .innerJoin(USER).on(USER.ID.eq(TEST_PLAN.CREATE_USER))
                    .where(TEST_PLAN.PROJECT_ID.eq(conditions.getProjectId()))
                    .and(TEST_PLAN.GROUP_ID.eq(conditions.getGroupId()))
                    .and(TEST_PLAN.NAME.like(request.getCondition().getKeyword()))
                    .and(TEST_PLAN.MODULE_ID.in(conditions.getModuleIds()))
                    .and(TEST_PLAN.ID.notIn(conditions.getHiddenIds()))
                    .or(TEST_PLAN.ID.in(conditions.getIncludeIds()));
            deleteIdList = queryChain().list().stream().map(TestPlan::getId).collect(Collectors.toList());

        }
        if (CollectionUtils.isEmpty(deleteIdList)) {
            return;
        }
        List<TestPlan> deleteTestPlanList = mapper.selectListByIds(deleteIdList);
        if (CollectionUtils.isNotEmpty(deleteTestPlanList)) {
            List<String> testPlanGroupList = new ArrayList<>();
            List<String> testPlanIdList = new ArrayList<>();
            deleteTestPlanList.forEach(testPlan -> {
                if (StringUtils.equals(testPlan.getType(), TestPlanConstants.TEST_PLAN_TYPE_GROUP)) {
                    testPlanGroupList.add(testPlan.getId());
                } else {
                    testPlanIdList.add(testPlan.getId());
                }
            });
            deleteByList(deleteIdList);
            deleteGroupByList(testPlanGroupList);
            testPlanLogService.saveBatchDeleteLog(deleteTestPlanList, operator, requestUrl, requestMethod);
        }
    }

    @Override
    public Page<TestPlanResponse> page(TestPlanTableRequest request) {
        QueryChain<TestPlan> queryChain = queryChain();
        Page<TestPlanResponse> testPlanResponses = mapper.paginateAs(Page.of(request.getCurrent(), request.getPageSize()), queryChain, TestPlanResponse.class);
        List<TestPlanResponse> records = testPlanResponses.getRecords();
        if (!records.isEmpty()) {
            records.forEach(item->{
                if (StringUtils.equals(item.getType(), TestPlanConstants.TEST_PLAN_TYPE_GROUP)) {

                }
            });
        }
        return testPlanResponses;
    }

    private void deleteByList(List<String> testPlanIds) {
        if (CollectionUtils.isNotEmpty(testPlanIds)) {
            BatchProcessUtils.consumerByString(testPlanIds, (deleteIds) -> {
                mapper.deleteBatchByIds(deleteIds);
                cascadeDeleteTestPlanIds(deleteIds);
            });
        }
    }

    private void cascadeDeleteTestPlanIds(List<String> testPlanIds) {
        // todo
    }

    private void deleteGroupByList(List<String> testPlanGroupIds) {
        if (CollectionUtils.isNotEmpty(testPlanGroupIds)) {
            BatchProcessUtils.consumerByString(testPlanGroupIds, (deleteGroupIds) -> {
                mapper.deleteBatchByIds(deleteGroupIds);
                cascadeDeleteTestPlanIds(deleteGroupIds);
                UpdateChain.of(TestPlan.class)
                        .set(TestPlan::getGroupId, "NONE")
                        .where(TestPlan::getGroupId).in(deleteGroupIds).update();
            });
        }
    }

    private void validateTestPlan(TestPlan testPlan) {
        if (StringUtils.isBlank(testPlan.getId())) {
            if (StringUtils.equals(testPlan.getGroupId(), TestPlanConstants.TEST_PLAN_DEFAULT_GROUP_ID)) {
                long count = queryChain().where(TEST_PLAN.GROUP_ID.eq(TestPlanConstants.TEST_PLAN_DEFAULT_GROUP_ID)).count();
                if (count >= MAX_TEST_PLAN_SIZE) {
                    throw new MSException(Translator.getWithArgs("test_plan.too_many", MAX_TEST_PLAN_SIZE));
                }
            }
            queryChain().clear();
            long count = queryChain().where(TEST_PLAN.NAME.eq(testPlan.getName()).and(TEST_PLAN.PROJECT_ID.eq(testPlan.getProjectId()))).count();
            if (count > 0) {
                throw new MSException(Translator.get("test_plan.name.exist") + ":" + testPlan.getName());
            }
        } else {
            long count = queryChain().where(TEST_PLAN.NAME.eq(testPlan.getName()).and(TEST_PLAN.PROJECT_ID.eq(testPlan.getProjectId()))
                    .and(TEST_PLAN.ID.ne(testPlan.getId()))).count();
            if (count > 0) {
                throw new MSException(Translator.get("test_plan.name.exist") + ":" + testPlan.getName());
            }
        }
    }

    private void checkModule(String moduleId) {
        if (!StringUtils.equals(moduleId, ModuleConstants.DEFAULT_NODE_ID)) {
            TestPlanModule testPlanModule = testPlanModuleMapper.selectOneById(moduleId);
            if (Objects.isNull(testPlanModule)) {
                throw new MSException(Translator.get("module.not.exist"));
            }
        }
    }
}
