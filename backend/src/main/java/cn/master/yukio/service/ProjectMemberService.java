package cn.master.yukio.service;

import cn.master.yukio.dto.user.UserExtendDTO;

import java.util.List;

/**
 * @author Created by 11's papa on 03/23/2024
 **/
public interface ProjectMemberService {
    List<UserExtendDTO> getMemberOption(String projectId, String keyword);
}
