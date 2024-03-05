package cn.master.yukio.service.impl;

import cn.master.yukio.constants.TemplateScene;
import cn.master.yukio.entity.OrganizationParameter;
import cn.master.yukio.mapper.OrganizationParameterMapper;
import cn.master.yukio.service.IOrganizationParameterService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.master.yukio.constants.OrganizationParameterConstants.*;

/**
 * 组织参数 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class OrganizationParameterServiceImpl extends ServiceImpl<OrganizationParameterMapper, OrganizationParameter> implements IOrganizationParameterService {

    @Override
    public String getValue(String orgId, String key) {
        OrganizationParameter organizationParameter = queryChain().where(OrganizationParameter::getOrganizationId).eq(orgId)
                .and(OrganizationParameter::getParamKey).eq(key).one();
        return Objects.isNull(organizationParameter) ? null : organizationParameter.getParamValue();
    }

    @Override
    public String getOrgTemplateEnableKeyByScene(String scene) {
        Map<String, String> sceneMap = new HashMap<>();
        sceneMap.put(TemplateScene.FUNCTIONAL.name(), ORGANIZATION_FUNCTIONAL_TEMPLATE_ENABLE_KEY);
        sceneMap.put(TemplateScene.BUG.name(), ORGANIZATION_BUG_TEMPLATE_ENABLE_KEY);
        sceneMap.put(TemplateScene.API.name(), ORGANIZATION_API_TEMPLATE_ENABLE_KEY);
        sceneMap.put(TemplateScene.UI.name(), ORGANIZATION_UI_TEMPLATE_ENABLE_KEY);
        sceneMap.put(TemplateScene.TEST_PLAN.name(), ORGANIZATION_TEST_PLAN_TEMPLATE_ENABLE_KEY);
        return sceneMap.get(scene);
    }
}
