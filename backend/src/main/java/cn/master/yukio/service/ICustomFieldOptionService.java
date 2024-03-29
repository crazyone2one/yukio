package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.CustomFieldOption;

import java.util.List;

/**
 * 自定义字段选项 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICustomFieldOptionService extends IService<CustomFieldOption> {

    List<CustomFieldOption> getByFieldIds(List<String> fieldIds);
}
