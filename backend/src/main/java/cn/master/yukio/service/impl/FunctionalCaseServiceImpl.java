package cn.master.yukio.service.impl;

import cn.master.yukio.constants.*;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.functional.CaseCustomFieldDTO;
import cn.master.yukio.dto.functional.FunctionalCaseAddRequest;
import cn.master.yukio.dto.functional.FunctionalCaseHistoryLogDTO;
import cn.master.yukio.entity.*;
import cn.master.yukio.mapper.FunctionalCaseBlobMapper;
import cn.master.yukio.mapper.FunctionalCaseMapper;
import cn.master.yukio.service.*;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.NumGenerator;
import cn.master.yukio.util.ServiceUtils;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.FunctionalCaseTableDef.FUNCTIONAL_CASE;
import static cn.master.yukio.entity.table.ProjectVersionTableDef.PROJECT_VERSION;

/**
 * 功能用例 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class FunctionalCaseServiceImpl extends ServiceImpl<FunctionalCaseMapper, FunctionalCase> implements IFunctionalCaseService {
    private static final String CASE_MODULE_COUNT_ALL = "all";

    private static final String ADD_FUNCTIONAL_CASE_FILE_LOG_URL = "/functional/case/add";
    private static final String UPDATE_FUNCTIONAL_CASE_FILE_LOG_URL = "/functional/case/update";
    private static final String FUNCTIONAL_CASE_BATCH_COPY_FILE_LOG_URL = "/functional/case/batch/copy";

    private static final String CASE_TABLE = "functional_case";
    private final FunctionalCaseBlobMapper functionalCaseBlobMapper;
    private final IFunctionalCaseCustomFieldService functionalCaseCustomFieldService;
    private final IFunctionalCaseAttachmentService functionalCaseAttachmentService;
    private final ICaseReviewService caseReviewService;
    private final ICaseReviewFunctionalCaseService caseReviewFunctionalCaseService;
    private final IOperationLogService operationLogService;

    @Override
    public FunctionalCase addFunctionalCase(FunctionalCaseAddRequest request, List<MultipartFile> files, String userId, String organizationId) {
        FunctionalCase functionalCase = addCase(request, userId);
        String caseId = functionalCase.getId();
        //  上传文件
        List<String> uploadFileIds = functionalCaseAttachmentService.uploadFile(request.getProjectId(), caseId, files, true, userId);
        // todo 上传副文本里的文件
        //  关联附件
        if (CollectionUtils.isNotEmpty(request.getRelateFileMetaIds())) {
            functionalCaseAttachmentService.association(request.getRelateFileMetaIds(), caseId, userId, ADD_FUNCTIONAL_CASE_FILE_LOG_URL, request.getProjectId());
        }
        // todo 处理复制时的已存在的文件

        addCaseReviewCase(request.getReviewId(), caseId, userId);
        // 记录日志
        FunctionalCaseHistoryLogDTO historyLogDTO = getImportLogModule(functionalCase);
        saveImportDataLog(functionalCase, new FunctionalCaseHistoryLogDTO(), historyLogDTO, userId, organizationId, OperationLogType.ADD.name(), OperationLogModule.CASE_MANAGEMENT_CASE_CREATE);
        return functionalCase;
    }

    private void saveImportDataLog(FunctionalCase functionalCase, FunctionalCaseHistoryLogDTO originalValue, FunctionalCaseHistoryLogDTO modifiedLogDTO, String userId, String organizationId, String type, String module) {
        LogDTO dto = new LogDTO(
                functionalCase.getProjectId(),
                organizationId,
                functionalCase.getId(),
                userId,
                type,
                module,
                functionalCase.getName());
        dto.setHistory(true);
        dto.setPath("/functional/case/import/excel");
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setModifiedValue(JsonUtils.toJsonByte(modifiedLogDTO));
        dto.setOriginalValue(JsonUtils.toJsonByte(originalValue));
        operationLogService.add(dto);
    }

    private FunctionalCaseHistoryLogDTO getImportLogModule(FunctionalCase functionalCase) {
        FunctionalCaseBlob functionalCaseBlob = QueryChain.of(FunctionalCaseBlob.class)
                .where(FunctionalCaseBlob::getCaseId).eq(functionalCase.getId()).one();
        //自定义字段
        List<FunctionalCaseCustomField> customFields = QueryChain.of(FunctionalCaseCustomField.class)
                .where(FunctionalCaseCustomField::getCaseId).eq(functionalCase.getId()).list();
        //附件  本地 + 文件库
        List<FunctionalCaseAttachment> caseAttachments = QueryChain.of(FunctionalCaseAttachment.class)
                .where(FunctionalCaseAttachment::getCaseId).eq(functionalCase.getId()).list();
        List<FileAssociation> fileAssociationList = QueryChain.of(FileAssociation.class)
                .where(FileAssociation::getSourceId).eq(functionalCase.getId()).list();
        return new FunctionalCaseHistoryLogDTO(functionalCase, functionalCaseBlob, customFields, caseAttachments, fileAssociationList);
    }

    private void addCaseReviewCase(String reviewId, String caseId, String userId) {
        if (StringUtils.isNotBlank(reviewId)) {
            caseReviewService.checkCaseReview(reviewId);
            caseReviewFunctionalCaseService.addCaseReviewFunctionalCase(caseId, userId, reviewId);
        }
    }

    private FunctionalCase addCase(FunctionalCaseAddRequest request, String userId) {
        FunctionalCase functionalCase = new FunctionalCase();
        BeanUtils.copyProperties(request, functionalCase);
        functionalCase.setNum(getNextNum(request.getProjectId()));
        functionalCase.setReviewStatus(FunctionalCaseReviewStatus.UN_REVIEWED.name());
        functionalCase.setPos(getNextOrder(request.getProjectId()));
        functionalCase.setLastExecuteResult(FunctionalCaseExecuteResult.UN_EXECUTED.name());
        functionalCase.setLatest(true);
        functionalCase.setCreateUser(userId);
        functionalCase.setVersionId(StringUtils.defaultIfBlank(request.getVersionId(), getDefaultVersion(request.getProjectId())));
        functionalCase.setTags(request.getTags());
        mapper.insert(functionalCase);
        functionalCase.setRefId(functionalCase.getId());
        mapper.update(functionalCase);
        //附属表
        FunctionalCaseBlob functionalCaseBlob = FunctionalCaseBlob.builder()
                .caseId(functionalCase.getId())
                .steps(StringUtils.defaultIfBlank(request.getSteps(), StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8))
                .textDescription(StringUtils.defaultIfBlank(request.getTextDescription(), StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8))
                .expectedResult(StringUtils.defaultIfBlank(request.getExpectedResult(), StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8))
                .prerequisite(StringUtils.defaultIfBlank(request.getPrerequisite(), StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8))
                .description(StringUtils.defaultIfBlank(request.getDescription(), StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8))
                .build();
        functionalCaseBlobMapper.insert(functionalCaseBlob);
        //保存自定义字段
        List<CaseCustomFieldDTO> customFields = request.getCustomFields();
        if (CollectionUtils.isNotEmpty(customFields)) {
            customFields = customFields.stream().distinct().collect(Collectors.toList());
            functionalCaseCustomFieldService.saveCustomField(functionalCase.getId(), customFields);
        }
        return functionalCase;
    }

    private String getDefaultVersion(String projectId) {
        return QueryChain.of(ProjectVersion.class).select(PROJECT_VERSION.ID)
                .where(PROJECT_VERSION.PROJECT_ID.eq(projectId).and(PROJECT_VERSION.LATEST.eq(true)))
                .limit(1).oneAs(String.class);
    }

    private Long getNextOrder(String projectId) {
        Long pos = queryChain().select(FUNCTIONAL_CASE.POS).where(FUNCTIONAL_CASE.PROJECT_ID.eq(projectId))
                .orderBy(FUNCTIONAL_CASE.POS.desc()).limit(1).oneAs(Long.class);
        return (pos == null ? 0 : pos) + ServiceUtils.POS_STEP;
    }

    private Long getNextNum(String projectId) {
        return NumGenerator.nextNum(projectId, ApplicationNumScope.CASE_MANAGEMENT);
    }
}
