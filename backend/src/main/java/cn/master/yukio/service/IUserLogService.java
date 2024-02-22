package cn.master.yukio.service;

import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.entity.User;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
public interface IUserLogService {
    List<LogDTO> getBatchAddLogs(@Valid List<User> userList, String requestPath);
}
