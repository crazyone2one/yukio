package cn.master.yukio.service;

import cn.master.yukio.dto.NodeSortDTO;
import cn.master.yukio.entity.TestPlanModule;
import org.springframework.validation.annotation.Validated;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
public interface TestPlanModuleLogService {

    void saveAddLog(TestPlanModule module, String operator, String requestUrl, String requestMethod);

    void saveUpdateLog(TestPlanModule oldModule, TestPlanModule newModule, String projectId, String operator, String requestUrl, String requestMethod);

    void saveDeleteLog(TestPlanModule deleteModule, String operator, String requestUrl, String requestMethod);

    void saveMoveLog(@Validated NodeSortDTO request, String operator, String requestUrl, String requestMethod);
}
