package cn.master.yukio.dto.functional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 03/07/2024
 **/
@Data
public class AssociationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "类型",allowableValues = {"EMPTY","NOT_EMPTY"})
    private String operatorType;


    @Schema(description = "用例类型",allowableValues = {"API","API_SCENARIO","UI_SCENARIO","LOAD"})
    private List<String> caseType;

}
