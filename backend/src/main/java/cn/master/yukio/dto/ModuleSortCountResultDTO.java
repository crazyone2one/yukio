package cn.master.yukio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@Data
@AllArgsConstructor
public class ModuleSortCountResultDTO {
    private boolean isRefreshPos;
    private long pos;
}
