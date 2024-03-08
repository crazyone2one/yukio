package cn.master.yukio.service.impl;

import cn.master.yukio.entity.CustomFieldOption;
import cn.master.yukio.mapper.CustomFieldOptionMapper;
import cn.master.yukio.service.ICustomFieldOptionService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义字段选项 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CustomFieldOptionServiceImpl extends ServiceImpl<CustomFieldOptionMapper, CustomFieldOption> implements ICustomFieldOptionService {

    @Override
    public List<CustomFieldOption> getByFieldIds(List<String> fieldIds) {
        if (CollectionUtils.isEmpty(fieldIds)) {
            return new ArrayList<>(0);
        }
        return queryChain().where(CustomFieldOption::getFieldId).in(fieldIds).list();
    }
}
