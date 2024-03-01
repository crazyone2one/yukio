package cn.master.yukio.service.impl;

import cn.master.yukio.constants.TestPlanResourceConfig;
import cn.master.yukio.entity.Project;
import cn.master.yukio.entity.TestPlan;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.service.TestPlanManagementService;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static cn.master.yukio.entity.table.ProjectTableDef.PROJECT;
import static cn.master.yukio.entity.table.TestPlanTableDef.TEST_PLAN;


/**
 * @author Created by 11's papa on 02/29/2024
 **/
@Service
@RequiredArgsConstructor
public class TestPlanManagementServiceImpl implements TestPlanManagementService {

    @Override
    public void checkModuleIsOpen(String resourceId, String resourceType, List<String> moduleMenus) {
        Project project = new Project();
        if (StringUtils.equals(resourceType, TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN)) {
            project = QueryChain.of(Project.class).where(PROJECT.ID.eq(
                    QueryChain.of(TestPlan.class).select(TEST_PLAN.PROJECT_ID).from(TEST_PLAN).where(TEST_PLAN.ID.eq(resourceId))
            )).one();
        } else if (StringUtils.equals(resourceType, TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN_MODULE)) {

        } else if (StringUtils.equals(resourceType, TestPlanResourceConfig.CHECK_TYPE_PROJECT)) {
            project = QueryChain.of(Project.class).where(PROJECT.ID.eq(resourceId)).one();
        } else {
            throw new MSException(Translator.get("project.module_menu.check.error"));
        }
        if (project == null || project.getModuleSetting().isEmpty()) {
            throw new MSException(Translator.get("project.module_menu.check.error"));
        }
        List<String> projectModuleMenus = project.getModuleSetting();
        if (!new HashSet<>(projectModuleMenus).containsAll(moduleMenus)) {
            throw new MSException(Translator.get("project.module_menu.check.error"));
        }
    }
}
