package cn.master.yukio.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.yukio.entity.FileAssociation;
import cn.master.yukio.service.IFileAssociationService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 文件资源关联 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/fileAssociation")
public class FileAssociationController {

    @Autowired
    private IFileAssociationService iFileAssociationService;

    /**
     * 添加文件资源关联。
     *
     * @param fileAssociation 文件资源关联
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody FileAssociation fileAssociation) {
        return iFileAssociationService.save(fileAssociation);
    }

    /**
     * 根据主键删除文件资源关联。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iFileAssociationService.removeById(id);
    }

    /**
     * 根据主键更新文件资源关联。
     *
     * @param fileAssociation 文件资源关联
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody FileAssociation fileAssociation) {
        return iFileAssociationService.updateById(fileAssociation);
    }

    /**
     * 查询所有文件资源关联。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<FileAssociation> list() {
        return iFileAssociationService.list();
    }

    /**
     * 根据文件资源关联主键获取详细信息。
     *
     * @param id 文件资源关联主键
     * @return 文件资源关联详情
     */
    @GetMapping("getInfo/{id}")
    public FileAssociation getInfo(@PathVariable Serializable id) {
        return iFileAssociationService.getById(id);
    }

    /**
     * 分页查询文件资源关联。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<FileAssociation> page(Page<FileAssociation> page) {
        return iFileAssociationService.page(page);
    }

}
