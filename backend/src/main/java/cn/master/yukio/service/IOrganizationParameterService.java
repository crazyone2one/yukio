package cn.master.yukio.service;

import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.OrganizationParameter;

/**
 * 组织参数 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IOrganizationParameterService extends IService<OrganizationParameter> {
    String getValue(String orgId, String key);

    String getOrgTemplateEnableKeyByScene(String scene);

}
