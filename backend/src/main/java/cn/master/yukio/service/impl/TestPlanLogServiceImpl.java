package cn.master.yukio.service.impl;

import cn.master.yukio.constants.OperationLogModule;
import cn.master.yukio.constants.OperationLogType;
import cn.master.yukio.constants.TestPlanConstants;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.LogDTOBuilder;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.TestPlan;
import cn.master.yukio.service.IOperationLogService;
import cn.master.yukio.service.TestPlanLogService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.master.yukio.entity.table.ProjectTableDef.PROJECT;

/**
 * @author Created by 11's papa on 02/29/2024
 **/
@Service
@RequiredArgsConstructor
public class TestPlanLogServiceImpl implements TestPlanLogService {
    private final IOperationLogService operationLogService;
    private final String logModule = OperationLogModule.TEST_PLAN;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAddLog(TestPlan module, String operator, String requestUrl, String requestMethod) {
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(module.getProjectId())).one();
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(module.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.ADD.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(module.getId())
                .content(generateTestPlanSimpleContent(module))
                .originalValue(JsonUtils.toJsonByte(module))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUpdateLog(TestPlan oldTestPlan, TestPlan newTestPlan, String projectId, String operator, String requestUrl, String requestMethod) {
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(projectId)).one();
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(projectId)
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.UPDATE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(newTestPlan.getId())
                .content(newTestPlan.getName())
                .originalValue(JsonUtils.toJsonByte(oldTestPlan))
                .modifiedValue(JsonUtils.toJsonByte(newTestPlan))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    @Override
    public void saveDeleteLog(TestPlan deleteTestPlan, String operator, String requestUrl, String requestMethod) {
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(deleteTestPlan.getProjectId())).one();
        operationLogService.add(getLogDTO(deleteTestPlan, operator, requestUrl, requestMethod, project));
    }

    private LogDTO getLogDTO(TestPlan testPlan, String operator, String requestUrl, String requestMethod, Project project) {
        return LogDTOBuilder.builder()
                .projectId(testPlan.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.DELETE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(testPlan.getId())
                .content(this.generateTestPlanDeleteContent(testPlan))
                .originalValue(JsonUtils.toJsonByte(testPlan))
                .createUser(operator)
                .build().getLogDTO();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchDeleteLog(List<TestPlan> testPlanList, String operator, String requestUrl, String requestMethod) {
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(testPlanList.get(0).getProjectId())).one();
        List<LogDTO> list = new ArrayList<>();
        for (TestPlan testPlan : testPlanList) {
            LogDTO logDTO = getLogDTO(testPlan, operator, requestUrl, requestMethod, project);
            list.add(logDTO);
        }
        operationLogService.batchAdd(list);
    }

    private String generateTestPlanDeleteContent(TestPlan testPlan) {
        StringBuilder content = new StringBuilder();
        if (StringUtils.equals(testPlan.getType(), TestPlanConstants.TEST_PLAN_TYPE_GROUP)) {
            content.append(Translator.get("log.delete.test_plan_group")).append(":").append(testPlan.getName()).append(StringUtils.SPACE);
        } else {
            content.append(Translator.get("log.delete.test_plan")).append(":").append(testPlan.getName()).append(StringUtils.SPACE);
        }
        return content.toString();
    }

    private String generateTestPlanSimpleContent(TestPlan testPlan) {
        StringBuilder content = new StringBuilder();
        if (StringUtils.equals(testPlan.getType(), TestPlanConstants.TEST_PLAN_TYPE_GROUP)) {
            content.append(Translator.get("test_plan.test_plan_group")).append(StringUtils.SPACE).append(testPlan.getName()).append(StringUtils.SPACE);
        } else {
            content.append(Translator.get("test_plan.test_plan")).append(StringUtils.SPACE).append(testPlan.getName()).append(StringUtils.SPACE);
        }
        return content.toString();
    }
}
