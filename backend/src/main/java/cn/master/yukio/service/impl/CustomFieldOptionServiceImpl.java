package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.CustomFieldOption;
import cn.master.yukio.mapper.CustomFieldOptionMapper;
import cn.master.yukio.service.ICustomFieldOptionService;
import org.springframework.stereotype.Service;

/**
 * 自定义字段选项 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CustomFieldOptionServiceImpl extends ServiceImpl<CustomFieldOptionMapper, CustomFieldOption> implements ICustomFieldOptionService {

}
