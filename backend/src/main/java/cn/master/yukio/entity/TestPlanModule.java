package cn.master.yukio.entity;

import com.mybatisflex.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

/**
 * 测试计划模块 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "test_plan_module")

public class TestPlanModule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 项目名称
     */
    private String projectId;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 模块ID
     */
    private String parentId;

    /**
     * 排序标识
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
    private TestPlanModule parent;

    @RelationOneToMany(selfField = "id", targetField = "parentId")
    private List<TestPlanModule> children;

    @Schema(description = "节点类型")
    @Column(ignore = true)
    private String type;

    @Schema(description = "节点路径（当前节点所在整棵树的路径）")
    @Column(ignore = true)
    private String path = "/";

    public void genModulePath(TestPlanModule parent) {
        if (parent != null) {
            path = parent.getPath() + "/" + this.getName();
        } else {
            path = "/" + this.getName();
        }
    }
}
