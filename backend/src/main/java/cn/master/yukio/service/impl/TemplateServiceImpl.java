package cn.master.yukio.service.impl;

import cn.master.yukio.constants.ProjectApplicationType;
import cn.master.yukio.constants.TemplateScopeType;
import cn.master.yukio.dto.project.TemplateCustomFieldRequest;
import cn.master.yukio.dto.project.TemplateSystemCustomFieldRequest;
import cn.master.yukio.dto.project.TemplateUpdateRequest;
import cn.master.yukio.dto.system.TemplateCustomFieldDTO;
import cn.master.yukio.dto.system.TemplateDTO;
import cn.master.yukio.entity.*;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.TemplateMapper;
import cn.master.yukio.resolver.field.AbstractCustomFieldResolver;
import cn.master.yukio.resolver.field.CustomFieldResolverFactory;
import cn.master.yukio.service.*;
import cn.master.yukio.util.ServiceUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.TemplateTableDef.TEMPLATE;
import static cn.master.yukio.handler.result.CommonResultCode.*;
import static cn.master.yukio.handler.result.ProjectResultCode.PROJECT_TEMPLATE_PERMISSION;

/**
 * 模版 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements ITemplateService {
    private final ICustomFieldService customFieldService;
    private final ITemplateCustomFieldService templateCustomFieldService;
    private final IProjectService projectService;
    private final IProjectApplicationService projectApplicationService;
    private final ICustomFieldOptionService customFieldOptionService;

    @Override
    public Template add(TemplateUpdateRequest request, String userId) {
        Template template = new Template();
        BeanUtils.copyProperties(request, template);
        template.setCreateUser(userId);
        checkProjectTemplateEnable(template.getScopeId(), template.getScene());
        template.setScopeType(TemplateScopeType.PROJECT.name());
        template.setRefId(null);
        return add(template, request.getCustomFields(), request.getSystemFields());
    }

    @Override
    public Template baseAdd(Template template, List<TemplateCustomFieldRequest> customFields, List<TemplateSystemCustomFieldRequest> systemFields) {
        checkAddExist(template);
        mapper.insert(template);
        templateCustomFieldService.addCustomFieldByTemplateId((template.getId()), customFields);
        templateCustomFieldService.addSystemFieldByTemplateId(template.getId(), parse2TemplateCustomFieldRequests(systemFields));
        return template;
    }

    @Override
    public Template update(TemplateUpdateRequest request) {
        Template template = new Template();
        BeanUtils.copyProperties(request, template);
        Template originTemplate = getWithCheck(template.getId());
        if (originTemplate.getInternal()) {
            // 内置模板不能修改名字
            template.setName(null);
        }
        checkProjectTemplateEnable(originTemplate.getScopeId(), originTemplate.getScene());
        template.setScopeId(originTemplate.getScopeId());
        template.setScene(originTemplate.getScene());
        checkProjectResourceExist(originTemplate);
        return update(template, request.getCustomFields(), request.getSystemFields());
    }

    @Override
    public Template update(Template template, List<TemplateCustomFieldRequest> customFields, List<TemplateSystemCustomFieldRequest> systemFields) {
        checkResourceExist(template.getId());
        checkUpdateExist(template);
        if (Objects.nonNull(customFields)) {
            templateCustomFieldService.deleteByTemplateIdAndSystem(template.getId(), false);
            templateCustomFieldService.addCustomFieldByTemplateId(template.getId(), customFields);
        }
        if (systemFields != null) {
            // 系统字段
            templateCustomFieldService.deleteByTemplateIdAndSystem(template.getId(), true);
            templateCustomFieldService.addSystemFieldByTemplateId(template.getId(), parse2TemplateCustomFieldRequests(systemFields));
        }
        mapper.update(template);
        return template;
    }

    private void checkUpdateExist(Template template) {
        if (StringUtils.isBlank(template.getName())) {
            return;
        }
        List<Template> list = queryChain().where(Template::getName).eq(template.getName())
                .and(Template::getScopeId).eq(template.getScopeId())
                .and(Template::getScene).eq(template.getScene())
                .and(Template::getId).ne(template.getId())
                .list();
        if (CollectionUtils.isNotEmpty(list)) {
            throw new MSException(TEMPLATE_EXIST);
        }
    }

    private void checkProjectResourceExist(Template template) {
        projectService.checkResourceExist(template.getScopeId());
    }

    @Override
    public Template getWithCheck(String id) {
        return checkResourceExist(id);
    }

    @Override
    public void delete(String id) {
        Template template = getWithCheck(id);
        checkProjectTemplateEnable(template.getScopeId(), template.getScene());
        checkDefault(template);
        Template template1 = checkResourceExist(id);
        checkInternal(template1);
        mapper.deleteById(id);
        templateCustomFieldService.remove(QueryChain.of(TemplateCustomField.class).where(TemplateCustomField::getTemplateId).eq(id));
    }

    private void checkInternal(Template template) {
        if (template.getInternal()) {
            throw new MSException(INTERNAL_TEMPLATE_PERMISSION);
        }
    }

    @Override
    public String getDefaultTemplateId(String projectId, String scene) {
        ProjectApplicationType.DEFAULT_TEMPLATE defaultTemplateParam = ProjectApplicationType.DEFAULT_TEMPLATE.getByTemplateScene(scene);
        ProjectApplication projectApplication = projectApplicationService.getByType(projectId, defaultTemplateParam.name());
        return projectApplication == null ? null : projectApplication.getTypeValue();
    }

    @Override
    public void setDefaultTemplate(String projectId, String id) {
        Template template = getById(id);
        if (Objects.isNull(template)) {

        }
        if (template == null) {
            // 为空check抛出异常
            template = getWithCheck(id);
        }
        String paramType = ProjectApplicationType.DEFAULT_TEMPLATE.getByTemplateScene(template.getScene()).name();
        ProjectApplication build = ProjectApplication.builder()
                .projectId(projectId).typeValue(id).type(paramType)
                .build();
        projectApplicationService.saveOrUpdate(build);
    }

    @Override
    public TemplateDTO getDefaultTemplateDTO(String projectId, String scene) {
        String defaultTemplateId = getDefaultTemplateId(projectId, scene);
        Template template;
        if (StringUtils.isBlank(defaultTemplateId)) {
            // 如果没有默认模板，则获取内置模板
            template = getInternalTemplate(projectId, scene);
        } else {
            template = mapper.selectOneById(defaultTemplateId);
            if (template == null) {
                // 如果默认模板查不到，则获取内置模板
                template = getInternalTemplate(projectId, scene);
            }
        }
        return getTemplateDTO(template);
    }

    private Template getInternalTemplate(String projectId, String scene) {
        return queryChain().where(Template::getScene).eq(scene)
                .and(Template::getScopeId).eq(projectId)
                .and(Template::getInternal).eq(true)
                .list().get(0);
    }

    @Override
    public TemplateDTO getTemplateDTO(Template template) {
        List<TemplateCustomField> templateCustomFields = templateCustomFieldService.getByTemplateId(template.getId());
        // 查找字段名称
        List<String> fieldIds = templateCustomFields.stream().map(TemplateCustomField::getFieldId).toList();
        List<CustomField> customFields = customFieldService.getByIds(fieldIds);
        Map<String, CustomField> fieldMap = customFields
                .stream()
                .collect(Collectors.toMap(CustomField::getId, customField -> {
                    if (customField.getInternal()) {
                        customField.setName(Translator.get("custom_field." + customField.getName()));
                    }
                    return customField;
                }));
        // 封装自定义字段信息
        List<TemplateCustomFieldDTO> fieldDTOS = templateCustomFields.stream()
                .filter(i -> !BooleanUtils.isTrue(i.getSystemField()) && fieldMap.containsKey(i.getFieldId()))
                .sorted(Comparator.comparingInt(TemplateCustomField::getPos))
                .map(i -> {
                    CustomField customField = fieldMap.get(i.getFieldId());
                    TemplateCustomFieldDTO templateCustomFieldDTO = new TemplateCustomFieldDTO();
                    BeanUtils.copyProperties(i, templateCustomFieldDTO);
                    templateCustomFieldDTO.setFieldName(customField.getName());
                    templateCustomFieldDTO.setType(customField.getType());
                    templateCustomFieldDTO.setInternal(customField.getInternal());
                    AbstractCustomFieldResolver customFieldResolver = CustomFieldResolverFactory.getResolver(customField.getType());
                    Object defaultValue = null;
                    try {
                        defaultValue = customFieldResolver.parse2Value(i.getDefaultValue());
                    } catch (Exception e) {
                        log.error("解析默认值失败，fieldId:{}", i.getFieldId(), e);
                    }
                    templateCustomFieldDTO.setDefaultValue(defaultValue);
                    return templateCustomFieldDTO;
                }).toList();
        List<String> ids = fieldDTOS.stream().map(TemplateCustomFieldDTO::getFieldId).toList();
        List<CustomFieldOption> fieldOptions = customFieldOptionService.getByFieldIds(ids);
        Map<String, List<CustomFieldOption>> collect = fieldOptions.stream().collect(Collectors.groupingBy(CustomFieldOption::getFieldId));

        fieldDTOS.forEach(item -> {
            item.setOptions(collect.get(item.getFieldId()));
        });
        // 封装系统字段信息
        List<TemplateCustomFieldDTO> systemFieldDTOS = templateCustomFields.stream()
                .filter(i -> BooleanUtils.isTrue(i.getSystemField()))
                .map(i -> {
                    TemplateCustomFieldDTO templateCustomFieldDTO = new TemplateCustomFieldDTO();
                    templateCustomFieldDTO.setFieldId(i.getFieldId());
                    templateCustomFieldDTO.setDefaultValue(i.getDefaultValue());
                    return templateCustomFieldDTO;
                }).toList();

        List<String> sysIds = systemFieldDTOS.stream().map(TemplateCustomFieldDTO::getFieldId).toList();
        List<CustomFieldOption> sysFieldOptions = customFieldOptionService.getByFieldIds(sysIds);
        Map<String, List<CustomFieldOption>> sysCollect = sysFieldOptions.stream().collect(Collectors.groupingBy(CustomFieldOption::getFieldId));

        systemFieldDTOS.forEach(item -> {
            item.setOptions(sysCollect.get(item.getFieldId()));
        });

        TemplateDTO templateDTO = new TemplateDTO();
        BeanUtils.copyProperties(template, templateDTO);
        templateDTO.setCustomFields(fieldDTOS);
        templateDTO.setSystemFields(systemFieldDTOS);
        return templateDTO;
    }

    private void checkDefault(Template template) {
        String defaultTemplateId = getDefaultTemplateId(template.getScopeId(), template.getScene());
        if (StringUtils.equals(template.getId(), defaultTemplateId)) {
            throw new MSException(DEFAULT_TEMPLATE_PERMISSION);
        }
    }

    private Template checkResourceExist(String id) {
        return ServiceUtils.checkResourceExist(mapper.selectOneById(id), "permission.organization_template.name");
    }

    private List<TemplateCustomFieldRequest> parse2TemplateCustomFieldRequests(List<TemplateSystemCustomFieldRequest> systemFields) {
        if (CollectionUtils.isEmpty(systemFields)) {
            return List.of();
        }
        return systemFields.stream().map(systemFiled -> {
            TemplateCustomFieldRequest templateCustomFieldRequest = new TemplateCustomFieldRequest();
            BeanUtils.copyProperties(systemFiled, templateCustomFieldRequest);
            templateCustomFieldRequest.setRequired(false);
            return templateCustomFieldRequest;
        }).toList();
    }

    private void checkAddExist(Template template) {
        List<Template> list = queryChain().where(TEMPLATE.SCOPE_ID.eq(template.getScopeId())
                        .and(TEMPLATE.SCENE.eq(template.getScene()))
                        .and(TEMPLATE.NAME.eq(template.getName())))
                .list();
        if (CollectionUtils.isNotEmpty(list)) {
            throw new MSException(TEMPLATE_EXIST);
        }
    }

    private Template add(Template template, List<TemplateCustomFieldRequest> customFields, List<TemplateSystemCustomFieldRequest> systemFields) {
        template.setInternal(false);
        return baseAdd(template, customFields, systemFields);
    }

    private void checkProjectTemplateEnable(String projectId, String scene) {
        Project project = QueryChain.of(Project.class).where(Project::getId).eq(projectId).one();
        if (customFieldService.isOrganizationTemplateEnable(project.getOrganizationId(), scene)) {
            throw new MSException(PROJECT_TEMPLATE_PERMISSION);
        }
    }
}
