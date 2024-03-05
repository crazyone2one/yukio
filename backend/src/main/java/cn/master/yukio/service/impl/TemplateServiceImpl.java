package cn.master.yukio.service.impl;

import cn.master.yukio.constants.ProjectApplicationType;
import cn.master.yukio.constants.TemplateScopeType;
import cn.master.yukio.dto.project.TemplateCustomFieldRequest;
import cn.master.yukio.dto.project.TemplateSystemCustomFieldRequest;
import cn.master.yukio.dto.project.TemplateUpdateRequest;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.ProjectApplication;
import cn.master.yukio.entity.Template;
import cn.master.yukio.entity.TemplateCustomField;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.TemplateMapper;
import cn.master.yukio.service.*;
import cn.master.yukio.util.ServiceUtils;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static cn.master.yukio.entity.table.TemplateTableDef.TEMPLATE;
import static cn.master.yukio.handler.result.CommonResultCode.*;
import static cn.master.yukio.handler.result.ProjectResultCode.PROJECT_TEMPLATE_PERMISSION;

/**
 * 模版 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements ITemplateService {
    private final ICustomFieldService customFieldService;
    private final ITemplateCustomFieldService templateCustomFieldService;
    private final IProjectService projectService;
    private final IProjectApplicationService projectApplicationService;

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
