package cn.master.yukio.dto.permission;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Data
public class Permission implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private Boolean enable = false;
    private Boolean license = false;
}
