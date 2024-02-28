package cn.master.yukio.dto.user.response;

import lombok.Data;

/**
 * @author Created by 11's papa on 02/27/2024
 **/
@Data
public class UserSelectOption {
    private String id;
    private String key;
    private String name;
    private String label;
    private boolean selected = false;
    private boolean closeable = true;
}
