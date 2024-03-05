package cn.master.yukio.entity;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 功能用例模块 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "functional_case_module")

public class FunctionalCaseModule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 名称
     */
    private String name;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 同一节点下的顺序
     */
    private Long pos;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    @RelationManyToOne(selfField = "parentId", targetField = "id")
    private FunctionalCaseModule parent;

    @RelationOneToMany(selfField = "id", targetField = "parentId")
    private List<FunctionalCaseModule> children;
    @Schema(description = "节点类型")
    @Column(ignore = true)
    private String type;

    @Schema(description = "节点路径（当前节点所在整棵树的路径）")
    @Column(ignore = true)
    private String path = "/";

    public void genModulePath(FunctionalCaseModule parent) {
        if (parent != null) {
            path = parent.getPath() + "/" + this.getName();
        } else {
            path = "/" + this.getName();
        }
    }

    public void addChild(FunctionalCaseModule node) {
        node.setParentId(this.getId());
        children.add(node);
    }
}
