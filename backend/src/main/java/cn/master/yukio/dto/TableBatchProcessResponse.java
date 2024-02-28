package cn.master.yukio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 02/27/2024
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableBatchProcessResponse {
    //@Schema(description = "全部数量")
    private long totalCount;
    //@Schema(description = "成功数量")
    private long successCount;
}
