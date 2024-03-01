package cn.master.yukio.dto.plan;

import cn.master.yukio.constants.TestPlanConstants;
import cn.master.yukio.dto.BaseCondition;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@Data
@NoArgsConstructor
public class TestPlanQueryConditions {
    //模块ID
    private List<String> moduleIds;

    //项目ID
    private String projectId;

    //测试计划所属GroupId
    private String groupId;

    //查询条件
    private BaseCondition condition = new BaseCondition();

    //隐藏的测试计划ID
    public List<String> hiddenIds = new ArrayList<>();

    //需要包含的ID
    public List<String> includeIds = new ArrayList<>();

    public TestPlanQueryConditions(List<String > moduleIds, String projectId, BaseCondition condition){
        this.groupId = TestPlanConstants.TEST_PLAN_DEFAULT_GROUP_ID;
        this.moduleIds = moduleIds;
        this.projectId = projectId;
        this.condition = condition;
    }
}
