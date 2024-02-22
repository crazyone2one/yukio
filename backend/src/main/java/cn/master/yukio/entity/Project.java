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
 * 项目 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "project")

public class Project implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Id
    private String id;

    /**
     * 项目编号
     */
    private Long num;

    /**
     * 组织ID
     */
    private String organizationId;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

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
     * 修改人
     */
    private String updateUser;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 是否删除
     */
    private Boolean deleted;

    /**
     * 删除人
     */
    private String deleteUser;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 模块设置
     */
    private String moduleSetting;

}
