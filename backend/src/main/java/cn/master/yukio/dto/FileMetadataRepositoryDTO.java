package cn.master.yukio.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@Data
public class FileMetadataRepositoryDTO implements Serializable {
    private String fileMetadataId;
    private String branch;
    private String commitId;

    private String commitMessage;

    @Serial
    private static final long serialVersionUID = 1L;
}