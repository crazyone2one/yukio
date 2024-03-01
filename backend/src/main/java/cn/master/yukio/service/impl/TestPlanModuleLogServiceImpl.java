package cn.master.yukio.service.impl;

import cn.master.yukio.constants.OperationLogModule;
import cn.master.yukio.constants.OperationLogType;
import cn.master.yukio.dto.BaseModule;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.LogDTOBuilder;
import cn.master.yukio.dto.NodeSortDTO;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.TestPlanModule;
import cn.master.yukio.mapper.ProjectMapper;
import cn.master.yukio.service.IOperationLogService;
import cn.master.yukio.service.TestPlanModuleLogService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@Service
@RequiredArgsConstructor
public class TestPlanModuleLogServiceImpl implements TestPlanModuleLogService {
    private final ProjectMapper projectMapper;
    private final IOperationLogService operationLogService;
    private String logModule = OperationLogModule.TEST_PLAN_MODULE;

    @Override
    public void saveAddLog(TestPlanModule module, String operator, String requestUrl, String requestMethod) {
        Project project = projectMapper.selectOneById(module.getProjectId());
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(module.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.ADD.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(module.getId())
                .content(module.getName())
                .originalValue(JsonUtils.toJsonByte(module))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    @Override
    public void saveUpdateLog(TestPlanModule oldModule, TestPlanModule newModule, String projectId, String operator, String requestUrl, String requestMethod) {
        Project project = projectMapper.selectOneById(projectId);
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(projectId)
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.UPDATE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(newModule.getId())
                .content(newModule.getName())
                .originalValue(JsonUtils.toJsonByte(oldModule))
                .modifiedValue(JsonUtils.toJsonByte(newModule))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    @Override
    public void saveDeleteLog(TestPlanModule deleteModule, String operator, String requestUrl, String requestMethod) {
        Project project = projectMapper.selectOneById(deleteModule.getProjectId());
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(deleteModule.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.DELETE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(deleteModule.getId())
                .content(deleteModule.getName() + " " + Translator.get("log.delete_module"))
                .originalValue(JsonUtils.toJsonByte(deleteModule))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    @Override
    public void saveMoveLog(NodeSortDTO request, String operator, String requestUrl, String requestMethod) {
        BaseModule moveNode = request.getNode();
        BaseModule previousNode = request.getPreviousNode();
        BaseModule nextNode = request.getNextNode();
        BaseModule parentModule = request.getParent();
        Project project = projectMapper.selectOneById(moveNode.getProjectId());
        String logContent;
        if (nextNode == null && previousNode == null) {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName();
        } else if (nextNode == null) {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName() + " " + previousNode.getName() + Translator.get("file.log.next");
        } else if (previousNode == null) {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName() + " " + nextNode.getName() + Translator.get("file.log.previous");
        } else {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName() + " " +
                    previousNode.getName() + Translator.get("file.log.next") + " " + nextNode.getName() + Translator.get("file.log.previous");
        }
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(moveNode.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.UPDATE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(moveNode.getId())
                .content(logContent)
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }
}
