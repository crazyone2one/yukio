package cn.master.yukio.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 操作日志内容详情 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "operation_log_blob")

public class OperationLogBlob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键,与operation_log表id一致
     */
    private String id;

    /**
     * 变更前内容
     */
    private byte[] originalValue;

    /**
     * 变更后内容
     */
    private byte[] modifiedValue;

}
