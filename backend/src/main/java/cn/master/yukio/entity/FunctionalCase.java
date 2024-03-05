package cn.master.yukio.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.mybatisflex.core.handler.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.util.List;

/**
 * 功能用例 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "functional_case")

public class FunctionalCase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 业务ID
     */
    private Long num;

    /**
     * 模块ID
     */
    private String moduleId;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 名称
     */
    private String name;

    /**
     * 评审结果：未评审/评审中/通过/不通过/重新提审
     */
    private String reviewStatus;

    /**
     * 标签（JSON)
     */
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 编辑模式：步骤模式/文本模式
     */
    private String caseEditType;

    /**
     * 自定义排序，间隔5000
     */
    private Long pos;

    /**
     * 版本ID
     */
    private String versionId;

    /**
     * 指向初始版本ID
     */
    private String refId;

    /**
     * 最近的执行结果：未执行/通过/失败/阻塞/跳过
     */
    private String lastExecuteResult;

    /**
     * 是否在回收站：0-否，1-是
     */
    private Boolean deleted;

    /**
     * 是否是公共用例：0-否，1-是
     */
    private Boolean publicCase;

    /**
     * 是否为最新版本：0-否，1-是
     */
    private Boolean latest;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 删除人
     */
    private String deleteUser;

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
     * 删除时间
     */
    private LocalDateTime deleteTime;

}
