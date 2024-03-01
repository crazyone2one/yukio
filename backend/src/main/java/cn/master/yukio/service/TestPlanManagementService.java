package cn.master.yukio.service;

import java.util.List;

/**
 * @author Created by 11's papa on 02/29/2024
 **/
public interface TestPlanManagementService {
    void checkModuleIsOpen(String resourceId, String resourceType, List<String> moduleMenus);
}
