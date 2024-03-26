package cn.master.yukio.service.impl;

import cn.master.yukio.constants.CustomFieldType;
import cn.master.yukio.constants.TemplateRequiredCustomField;
import cn.master.yukio.constants.TemplateScene;
import cn.master.yukio.dto.BasePageRequest;
import cn.master.yukio.dto.CustomFieldDTO;
import cn.master.yukio.entity.CustomField;
import cn.master.yukio.entity.CustomFieldOption;
import cn.master.yukio.entity.TemplateCustomField;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.CustomFieldMapper;
import cn.master.yukio.mapper.OrganizationMapper;
import cn.master.yukio.service.ICustomFieldOptionService;
import cn.master.yukio.service.ICustomFieldService;
import cn.master.yukio.service.IOrganizationParameterService;
import cn.master.yukio.service.IUserService;
import cn.master.yukio.util.ServiceUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.yukio.entity.table.TemplateCustomFieldTableDef.TEMPLATE_CUSTOM_FIELD;
import static cn.master.yukio.handler.result.CommonResultCode.TEMPLATE_SCENE_ILLEGAL;

/**
 * 自定义字段 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CustomFieldServiceImpl extends ServiceImpl<CustomFieldMapper, CustomField> implements ICustomFieldService {
    private final IOrganizationParameterService organizationParameterService;
    private final OrganizationMapper organizationMapper;
    private final IUserService iUserService;
    private final ICustomFieldOptionService customFieldOptionService;
    private static final String CREATE_USER = "CREATE_USER";

    @Override
    public boolean isOrganizationTemplateEnable(String orgId, String scene) {
        String key = organizationParameterService.getOrgTemplateEnableKeyByScene(scene);
        String value = organizationParameterService.getValue(orgId, key);
        return !StringUtils.equals(BooleanUtils.toStringTrueFalse(false), value);
    }

    @Override
    public CustomField getWithCheck(String id) {
        checkResourceExist(id);
        return getById(id);
    }

    @Override
    public List<CustomField> getByIds(List<String> fieldIds) {
        if (CollectionUtils.isEmpty(fieldIds)) {
            return new ArrayList<>(0);
        }
        return queryChain().where(CustomField::getId).in(fieldIds).list();
    }

    @Override
    public Page<CustomFieldDTO> page(String organizationId, String scene, BasePageRequest page) {
        ServiceUtils.checkResourceExist(organizationMapper.selectOneById(organizationId), "permission.system_organization_project.name");
        checkScene(scene);
        Page<CustomFieldDTO> customFieldsPage = getPageByScopeIdAndScene(organizationId, scene, page);
        List<CustomFieldDTO> customFields = customFieldsPage.getRecords();
        if (CollectionUtils.isNotEmpty(customFields)) {
            List<String> userIds = customFields.stream().map(CustomField::getCreateUser).toList();
            List<String> usedFieldIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(customFields)) {
                usedFieldIds.addAll(selectUsedFieldIds(customFields.stream().map(CustomField::getId).toList()));
            }
            Map<String, String> userNameMap = iUserService.getUserNameMap(userIds);
            List<CustomFieldOption> customFieldOptions = customFieldOptionService.getByFieldIds(customFields.stream().map(CustomField::getId).toList());
            Map<String, List<CustomFieldOption>> optionMap = customFieldOptions.stream().collect(Collectors.groupingBy(CustomFieldOption::getFieldId));
            customFields.forEach(item -> {
                item.setCreateUser(userNameMap.get(item.getCreateUser()));
                CustomFieldDTO customFieldDTO = new CustomFieldDTO();
                BeanUtils.copyProperties(item, customFieldDTO);
                //判断有没有用到
                if (usedFieldIds.contains(item.getId())) {
                    customFieldDTO.setUsed(true);
                }
                customFieldDTO.setOptions(optionMap.get(item.getId()));
                if (CustomFieldType.getHasOptionValueSet().contains(customFieldDTO.getType()) && customFieldDTO.getOptions() == null) {
                    customFieldDTO.setOptions(List.of());
                }
                if (StringUtils.equalsAny(item.getType(), CustomFieldType.MEMBER.name(), CustomFieldType.MULTIPLE_MEMBER.name())) {
                    // 成员选项添加默认的选项
                    CustomFieldOption createUserOption = new CustomFieldOption();
                    createUserOption.setFieldId(item.getId());
                    createUserOption.setText(Translator.get("message.domain.create_user"));
                    createUserOption.setValue(CREATE_USER);
                    createUserOption.setInternal(false);
                    customFieldDTO.setOptions(List.of(createUserOption));
                }
                if (BooleanUtils.isTrue(item.getInternal())) {
                    // 设置哪些内置字段是模板里必选的
                    Set<String> templateRequiredCustomFieldSet = Arrays.stream(TemplateRequiredCustomField.values())
                            .map(TemplateRequiredCustomField::getName)
                            .collect(Collectors.toSet());
                    customFieldDTO.setTemplateRequired(templateRequiredCustomFieldSet.contains(item.getName()));
                    // 翻译内置字段名称
                    customFieldDTO.setName(translateInternalField(item.getName()));
                }
            });
        }
        return customFieldsPage;
    }

    private Page<CustomFieldDTO> getPageByScopeIdAndScene(String scopeId, String scene, BasePageRequest pageRequest) {
        return queryChain().where(CustomField::getScopeId).eq(scopeId)
                .and(CustomField::getScene).eq(scene)
                .pageAs(Page.of(pageRequest.getCurrent(), pageRequest.getPageSize()), CustomFieldDTO.class);
    }

    public String translateInternalField(String filedName) {
        return Translator.get("custom_field." + filedName);
    }

    private List<String> selectUsedFieldIds(List<String> fieldIds) {
        return QueryChain.of(TemplateCustomField.class)
                .select(QueryMethods.distinct(TEMPLATE_CUSTOM_FIELD.FIELD_ID))
                .where(TEMPLATE_CUSTOM_FIELD.FIELD_ID.in(fieldIds))
                .listAs(String.class);
    }

    @Override
    public List<CustomField> getByScopeIdAndScene(String scopeId, String scene) {
        return queryChain().where(CustomField::getScopeId).eq(scopeId)
                .and(CustomField::getScene).eq(scene).list();
    }

    private void checkScene(String scene) {
        Arrays.stream(TemplateScene.values()).map(TemplateScene::name)
                .filter(item -> item.equals(scene))
                .findFirst()
                .orElseThrow(() -> new MSException(TEMPLATE_SCENE_ILLEGAL));
    }

    private CustomField checkResourceExist(String id) {
        return ServiceUtils.checkResourceExist(getById(id), "permission.organization_custom_field.name");
    }
}
