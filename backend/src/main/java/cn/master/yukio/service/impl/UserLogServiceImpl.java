package cn.master.yukio.service.impl;

import cn.master.yukio.constants.HttpMethodConstants;
import cn.master.yukio.constants.OperationLogConstants;
import cn.master.yukio.constants.OperationLogModule;
import cn.master.yukio.constants.OperationLogType;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.entity.User;
import cn.master.yukio.service.IUserLogService;
import cn.master.yukio.util.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@Service
public class UserLogServiceImpl implements IUserLogService {
    @Override
    public List<LogDTO> getBatchAddLogs(List<User> userList, String requestPath) {
        List<LogDTO> logs = new ArrayList<>();
        userList.forEach(user -> {
            LogDTO log = new LogDTO();
            log.setProjectId(OperationLogConstants.SYSTEM);
            log.setOrganizationId(OperationLogConstants.SYSTEM);
            log.setType(OperationLogType.ADD.name());
            log.setModule(OperationLogModule.SETTING_SYSTEM_USER_SINGLE);
            log.setMethod(HttpMethodConstants.POST.name());
            log.setPath(requestPath);
            log.setSourceId(user.getId());
            log.setContent(user.getName() + "(" + user.getEmail() + ")");
            log.setOriginalValue(JsonUtils.toJsonByte(user));
            log.setCreateUser(user.getCreateUser());
            logs.add(log);
        });
        return logs;
    }
}
