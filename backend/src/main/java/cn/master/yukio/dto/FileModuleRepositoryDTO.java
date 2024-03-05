package cn.master.yukio.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@Data
public class FileModuleRepositoryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String fileModuleId;

    private String platform;

    private String url;

    private String token;

    private String userName;
}