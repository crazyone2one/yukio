package cn.master.yukio.dto.plan;

import cn.master.yukio.dto.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@EqualsAndHashCode(callSuper = false)
@Data
public class TestPlanTableRequest extends BasePageRequest {
    @Schema(description = "模块ID(根据模块树查询时要把当前节点以及子节点都放在这里。)")
    private List<String> moduleIds;

    @Schema(description = "项目ID")
    @NotBlank(message = "{id must not be blank}")
    private String projectId;

    public TestPlanTableRequest() {
        this.setCurrent(1);
        this.setPageSize(5);
    }

    //没有查询条件
    public boolean conditionIsEmpty() {
        return StringUtils.isEmpty(this.getKeyword()) && MapUtils.isEmpty(this.getFilter()) && MapUtils.isEmpty(this.getCombine());
    }

    @Override
    public String getSortString() {
        if (StringUtils.isEmpty(super.getSortString())) {
            return "t.update_time desc";
        } else {
            return "t." + super.getSortString();
        }

    }
}
