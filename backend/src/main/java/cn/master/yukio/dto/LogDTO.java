package cn.master.yukio.dto;

import cn.master.yukio.entity.OperationLog;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LogDTO extends OperationLog {
    private byte[] originalValue;
    private byte[] modifiedValue;
    private Boolean history = false;

    public LogDTO() {
    }

    public LogDTO(String projectId, String organizationId, String sourceId, String createUser, String type, String module, String content) {
        this.setProjectId(projectId);
        this.setOrganizationId(organizationId);
        this.setSourceId(sourceId);
        this.setCreateUser(createUser);
        this.setType(type);
        this.setModule(module);
        this.setContent(content);
        this.setCreateTime(LocalDateTime.now());
    }
}
