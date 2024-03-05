package cn.master.yukio.service;

import cn.master.yukio.file.FileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public interface FileService {
    String upload(MultipartFile file, FileRequest request) throws Exception;

    String upload(InputStream inputStream, FileRequest request) throws Exception;

    String upload(byte[] file, FileRequest request) throws Exception;

    byte[] download(FileRequest request) throws Exception;

    void deleteFile(FileRequest request) throws Exception;
}
