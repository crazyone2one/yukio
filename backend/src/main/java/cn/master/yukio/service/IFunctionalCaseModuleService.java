package cn.master.yukio.service;

import cn.master.yukio.dto.functional.FunctionalCaseModuleCreateRequest;
import cn.master.yukio.dto.functional.FunctionalCaseModuleUpdateRequest;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.FunctionalCaseModule;

import java.util.List;

/**
 * 功能用例模块 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IFunctionalCaseModuleService extends IService<FunctionalCaseModule> {

    String add(FunctionalCaseModuleCreateRequest request, String userId);

    void update(FunctionalCaseModuleUpdateRequest request, String userId);

    void deleteModule(String id);

    List<FunctionalCaseModule> getTree(String projectId);
}
