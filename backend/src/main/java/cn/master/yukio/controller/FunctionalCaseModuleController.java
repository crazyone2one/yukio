package cn.master.yukio.controller;

import cn.master.yukio.dto.functional.FunctionalCaseModuleCreateRequest;
import cn.master.yukio.dto.functional.FunctionalCaseModuleUpdateRequest;
import cn.master.yukio.entity.FunctionalCaseModule;
import cn.master.yukio.service.IFunctionalCaseModuleService;
import cn.master.yukio.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 功能用例模块 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/functional/case/module")
@RequiredArgsConstructor
public class FunctionalCaseModuleController {

    private final IFunctionalCaseModuleService iFunctionalCaseModuleService;

    /**
     * 添加功能用例模块。
     *
     * @param request 功能用例模块
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public String save(@RequestBody @Validated FunctionalCaseModuleCreateRequest request) {
        return iFunctionalCaseModuleService.add(request, SessionUtils.getUserId());
    }

    /**
     * 根据主键删除功能用例模块。
     *
     * @param id 主键
     */
    @DeleteMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        iFunctionalCaseModuleService.deleteModule(id);
    }

    /**
     * 根据主键更新功能用例模块。
     *
     * @param request 功能用例模块
     */
    @PostMapping("update")
    public void update(@RequestBody @Validated FunctionalCaseModuleUpdateRequest request) {
        iFunctionalCaseModuleService.update(request, SessionUtils.getUserId());
    }

    /**
     * 查询所有功能用例模块。
     *
     * @return 所有数据
     */
    @GetMapping("tree/{projectId}")
    public List<FunctionalCaseModule> list(@PathVariable String projectId) {
        return iFunctionalCaseModuleService.getTree(projectId);
    }

    /**
     * 根据功能用例模块主键获取详细信息。
     *
     * @param id 功能用例模块主键
     * @return 功能用例模块详情
     */
    @GetMapping("getInfo/{id}")
    public FunctionalCaseModule getInfo(@PathVariable Serializable id) {
        return iFunctionalCaseModuleService.getById(id);
    }

    /**
     * 分页查询功能用例模块。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<FunctionalCaseModule> page(Page<FunctionalCaseModule> page) {
        return iFunctionalCaseModuleService.page(page);
    }

}
