package cn.master.yukio.service.impl;

import cn.master.yukio.dto.project.TemplateCustomFieldRequest;
import cn.master.yukio.dto.system.CustomFieldDao;
import cn.master.yukio.entity.CustomField;
import cn.master.yukio.entity.TemplateCustomField;
import cn.master.yukio.mapper.TemplateCustomFieldMapper;
import cn.master.yukio.resolver.field.AbstractCustomFieldResolver;
import cn.master.yukio.resolver.field.CustomFieldResolverFactory;
import cn.master.yukio.service.ICustomFieldService;
import cn.master.yukio.service.ITemplateCustomFieldService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 模板和字段的关联关系 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TemplateCustomFieldServiceImpl extends ServiceImpl<TemplateCustomFieldMapper, TemplateCustomField> implements ITemplateCustomFieldService {
    private final ICustomFieldService customFieldService;

    @Override
    public void addCustomFieldByTemplateId(String id, List<TemplateCustomFieldRequest> customFieldRequests) {
        if (CollectionUtils.isEmpty(customFieldRequests)) {
            return;
        }
        // 过滤下不存在的字段
        List<String> ids = customFieldRequests.stream().map(TemplateCustomFieldRequest::getFieldId).toList();
        Set<String> fieldIdSet = QueryChain.of(CustomField.class).where(CustomField::getId).in(ids).list()
                .stream()
                .map(CustomField::getId)
                .collect(Collectors.toSet());
        customFieldRequests = customFieldRequests.stream()
                .filter(item -> fieldIdSet.contains(item.getFieldId()))
                .toList();
        this.addByTemplateId(id, customFieldRequests, false);
    }

    @Override
    public void addSystemFieldByTemplateId(String id, List<TemplateCustomFieldRequest> customFieldRequests) {
        if (CollectionUtils.isEmpty(customFieldRequests)) {
            return;
        }
        addByTemplateId(id, customFieldRequests, true);
    }

    @Override
    public void deleteByTemplateIdAndSystem(String id, boolean isSystem) {
        QueryChain<TemplateCustomField> qw = QueryChain.of(TemplateCustomField.class)
                .where(TemplateCustomField::getTemplateId).eq(id)
                .and(TemplateCustomField::getSystemField).eq(isSystem);
        mapper.deleteByQuery(qw);
    }

    @Override
    public List<TemplateCustomField> getByTemplateId(String id) {
        return queryChain().where(TemplateCustomField::getTemplateId).eq(id).list();
    }

    private void addByTemplateId(String templateId, List<TemplateCustomFieldRequest> customFieldRequests, boolean isSystem) {
        AtomicReference<Integer> pos = new AtomicReference<>(0);
        List<TemplateCustomField> templateCustomFields = customFieldRequests.stream().map(field -> {
            TemplateCustomField templateCustomField = new TemplateCustomField();
            BeanUtils.copyProperties(field, templateCustomField);
            templateCustomField.setTemplateId(templateId);
            templateCustomField.setPos(pos.getAndSet(pos.get() + 1));
            templateCustomField.setDefaultValue(isSystem ? field.getDefaultValue().toString() : parseDefaultValue(field));
            templateCustomField.setSystemField(isSystem);
            return templateCustomField;
        }).toList();
        if (CollectionUtils.isNotEmpty(templateCustomFields)) {
            mapper.insertBatch(templateCustomFields);
        }
    }

    private String parseDefaultValue(TemplateCustomFieldRequest field) {
        CustomField customField = customFieldService.getWithCheck(field.getFieldId());
        AbstractCustomFieldResolver customFieldResolver = CustomFieldResolverFactory.getResolver(customField.getType());
        CustomFieldDao customFieldDao = new CustomFieldDao();
        BeanUtils.copyProperties(customField, customFieldDao);
        customFieldDao.setRequired(false);
        customFieldResolver.validate(customFieldDao, field.getDefaultValue());
        return customFieldResolver.parse2String(field.getDefaultValue());
    }
}
