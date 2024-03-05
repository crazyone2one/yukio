package cn.master.yukio.dto.system;

import cn.master.yukio.entity.CustomField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomFieldDao extends CustomField implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean required;

    private String defaultValue;

    private Object value;
}
