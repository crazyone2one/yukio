package cn.master.yukio.dto.project;

import cn.master.yukio.dto.user.UserExtendDTO;
import cn.master.yukio.entity.Project;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * @author Created by 11's papa on 02/22/2024
 **/

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectDTO extends Project {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long memberCount;
    private String organizationName;
    private Boolean projectCreateUserIsAdmin;
    private List<String> moduleIds;
    private Integer remainDayCount;
    //private List<ProjectResourcePoolDTO> resourcePoolList;
    private List<UserExtendDTO> adminList;
}
