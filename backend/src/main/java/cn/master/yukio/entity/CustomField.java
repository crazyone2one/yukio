package cn.master.yukio.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 自定义字段 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "custom_field")

public class CustomField implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自定义字段ID
     */
    @Id
    private String id;

    /**
     * 自定义字段名称
     */
    private String name;

    /**
     * 使用场景
     */
    private String scene;

    /**
     * 自定义字段类型
     */
    private String type;

    /**
     * 自定义字段备注
     */
    private String remark;

    /**
     * 是否是内置字段
     */
    private Boolean internal;

    /**
     * 组织或项目级别字段（PROJECT, ORGANIZATION）
     */
    private String scopeType;

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
     * 项目字段所关联的组织字段ID
     */
    private String refId;

    /**
     * 是否需要手动输入选项key
     */
    private Boolean enableOptionKey;

    /**
     * 组织或项目ID
     */
    private String scopeId;

}
