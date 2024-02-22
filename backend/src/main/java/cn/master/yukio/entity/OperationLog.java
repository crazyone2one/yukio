package cn.master.yukio.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 操作日志 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "operation_log")
public class OperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 组织id
     */
    private String organizationId;

    /**
     * 操作时间
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    private String createUser;

    /**
     * 资源id
     */
    private String sourceId;

    /**
     * 操作方法
     */
    private String method;

    /**
     * 操作类型/add/update/delete
     */
    private String type;

    /**
     * 操作模块/api/case/scenario/ui
     */
    private String module;

    /**
     * 操作详情
     */
    private String content;

    /**
     * 操作路径
     */
    private String path;

}
