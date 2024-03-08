package cn.master.yukio.service.impl;

import cn.master.yukio.constants.*;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.functional.*;
import cn.master.yukio.entity.*;
import cn.master.yukio.mapper.FunctionalCaseBlobMapper;
import cn.master.yukio.mapper.FunctionalCaseMapper;
import cn.master.yukio.service.*;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.NumGenerator;
import cn.master.yukio.util.ServiceUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.master.yukio.entity.table.FunctionalCaseTableDef.FUNCTIONAL_CASE;
import static cn.master.yukio.entity.table.ProjectVersionTableDef.PROJECT_VERSION;
import static cn.master.yukio.entity.table.CaseReviewFunctionalCaseTableDef.CASE_REVIEW_FUNCTIONAL_CASE;

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
    private final ICustomFieldOptionService customFieldOptionService;
    private final IUserService userService;

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

    @Override
    public Page<FunctionalCase> getFunctionalCasePage(FunctionalCasePageRequest request, boolean deleted) {
        QueryChain<FunctionalCase> queryChain = queryChain().select(FUNCTIONAL_CASE.ALL_COLUMNS, PROJECT_VERSION.NAME.as("versionName"))
                .leftJoin(PROJECT_VERSION).on(PROJECT_VERSION.ID.eq(FUNCTIONAL_CASE.VERSION_ID))
                .where(FUNCTIONAL_CASE.PROJECT_ID.eq(request.getProjectId()))
                .and(FUNCTIONAL_CASE.DELETED.eq(deleted))
                .and(FUNCTIONAL_CASE.MODULE_ID.in(request.getModuleIds()))
                .and(FUNCTIONAL_CASE.NAME.like(request.getKeyword())
                        .or(FUNCTIONAL_CASE.NUM.like(request.getKeyword()))
                        .or(FUNCTIONAL_CASE.TAGS.like(request.getKeyword())))
                .and(FUNCTIONAL_CASE.ID.notIn(
                        QueryChain.of(CaseReviewFunctionalCase.class).where(CASE_REVIEW_FUNCTIONAL_CASE.REVIEW_ID.eq(request.getReviewId()))
                ).when(StringUtils.isNoneBlank(request.getReviewId())))
                .and(FUNCTIONAL_CASE.ID.notIn(request.getExcludeIds()));
        Page<FunctionalCase> page = mapper.paginate(Page.of(request.getCurrent(), request.getPageSize()), queryChain);
        return handleCustomFields(page);
    }

    private Page<FunctionalCase> handleCustomFields(Page<FunctionalCase> page) {
        List<FunctionalCase> functionalCaseLists = page.getRecords();
        List<String> ids = functionalCaseLists.stream().map(FunctionalCase::getId).toList();
        Map<String, List<FunctionalCaseCustomFieldDTO>> collect = getCaseCustomFiledMap(ids);
        Set<String> userIds = extractUserIds(functionalCaseLists);
        Map<String, String> userMap = userService.getUserNameMap(new ArrayList<>(userIds));
        functionalCaseLists.forEach(functionalCasePageDTO -> {
            functionalCasePageDTO.setCustomFields(collect.get(functionalCasePageDTO.getId()));
            functionalCasePageDTO.setCreateUserName(userMap.get(functionalCasePageDTO.getCreateUser()));
            functionalCasePageDTO.setUpdateUserName(userMap.get(functionalCasePageDTO.getUpdateUser()));
            functionalCasePageDTO.setDeleteUserName(userMap.get(functionalCasePageDTO.getDeleteUser()));
        });
        return page;
    }

    private Set<String> extractUserIds(List<FunctionalCase> list) {
        return list.stream()
                .flatMap(functionalCasePageDTO -> Stream.of(functionalCasePageDTO.getUpdateUser(), functionalCasePageDTO.getDeleteUser(), functionalCasePageDTO.getCreateUser()))
                .collect(Collectors.toSet());
    }

    private Map<String, List<FunctionalCaseCustomFieldDTO>> getCaseCustomFiledMap(List<String> ids) {
        List<FunctionalCaseCustomFieldDTO> customFields = functionalCaseCustomFieldService.getCustomFieldsByCaseIds(ids);
        customFields.forEach(customField -> {
            if (customField.getInternal()) {
                customField.setFieldName(translateInternalField(customField.getFieldName()));
            }
        });
        List<String> fieldIds = customFields.stream().map(FunctionalCaseCustomFieldDTO::getFieldId).toList();
        List<CustomFieldOption> fieldOptions = customFieldOptionService.getByFieldIds(fieldIds);
        Map<String, List<CustomFieldOption>> customOptions = fieldOptions.stream().collect(Collectors.groupingBy(CustomFieldOption::getFieldId));
        customFields.forEach(customField -> {
            customField.setOptions(customOptions.get(customField.getFieldId()));
        });
        return customFields.stream().collect(Collectors.groupingBy(FunctionalCaseCustomFieldDTO::getCaseId));
    }

    private String translateInternalField(String filedName) {
        return Translator.get("custom_field." + filedName);
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
