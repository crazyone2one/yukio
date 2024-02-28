package cn.master.yukio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO implements Serializable {
    @JsonProperty("value")
    private String id;

    @JsonProperty("label")
    private String name;
}
