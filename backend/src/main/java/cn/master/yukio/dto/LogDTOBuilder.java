package cn.master.yukio.dto;

import lombok.Builder;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@Builder
public class LogDTOBuilder {
    private String projectId;
    private String organizationId;
    private String sourceId;
    private String createUser;
    private String type;
    private String method;
    private String module;
    private String content;
    private String path;
    private byte[] originalValue;
    private byte[] modifiedValue;

    public LogDTO getLogDTO() {
        LogDTO logDTO = new LogDTO(projectId, organizationId, sourceId, createUser, type, module, content);
        logDTO.setPath(path);
        logDTO.setMethod(method);
        logDTO.setOriginalValue(originalValue);
        logDTO.setModifiedValue(modifiedValue);
        return logDTO;
    }
}
