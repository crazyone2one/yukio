package cn.master.yukio.entity;

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
 * 文件资源关联 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "file_association")

public class FileAssociation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 资源类型
     */
    private String sourceType;

    /**
     * 资源ID
     */
    private String sourceId;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件同版本ID
     */
    private String fileRefId;

    /**
     * 文件版本
     */
    private String fileVersion;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 是否删除
     */
    private Boolean deleted;

    /**
     * 删除时的文件名称
     */
    private String deletedFileName;

}
