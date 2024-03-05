package cn.master.yukio.controller;

import cn.master.yukio.dto.functional.FunctionalCaseAddRequest;
import cn.master.yukio.entity.FunctionalCase;
import cn.master.yukio.service.IFunctionalCaseService;
import cn.master.yukio.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * 功能用例 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/functional/case")
@RequiredArgsConstructor
public class FunctionalCaseController {

    private final IFunctionalCaseService iFunctionalCaseService;

    /**
     * 添加功能用例。
     *
     * @param request 功能用例
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public FunctionalCase save(@Validated @RequestPart("request") FunctionalCaseAddRequest request, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        String userId = SessionUtils.getUserId();
        String organizationId = SessionUtils.getCurrentOrganizationId();
        return iFunctionalCaseService.addFunctionalCase(request, files, userId, organizationId);
    }

    /**
     * 根据主键删除功能用例。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iFunctionalCaseService.removeById(id);
    }

    /**
     * 根据主键更新功能用例。
     *
     * @param functionalCase 功能用例
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody FunctionalCase functionalCase) {
        return iFunctionalCaseService.updateById(functionalCase);
    }

    /**
     * 查询所有功能用例。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<FunctionalCase> list() {
        return iFunctionalCaseService.list();
    }

    /**
     * 根据功能用例主键获取详细信息。
     *
     * @param id 功能用例主键
     * @return 功能用例详情
     */
    @GetMapping("getInfo/{id}")
    public FunctionalCase getInfo(@PathVariable String id) {
        return iFunctionalCaseService.getById(id);
    }

    /**
     * 分页查询功能用例。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<FunctionalCase> page(Page<FunctionalCase> page) {
        return iFunctionalCaseService.page(page);
    }

}
