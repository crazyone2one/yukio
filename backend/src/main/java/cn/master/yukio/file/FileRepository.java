package cn.master.yukio.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public interface FileRepository {
    String saveFile(MultipartFile file, FileRequest request) throws Exception;

    String saveFile(byte[] bytes, FileRequest request) throws Exception;

    String saveFile(InputStream inputStream, FileRequest request) throws Exception;

    void delete(FileRequest request) throws Exception;

    void deleteFolder(FileRequest request) throws Exception;

    byte[] getFile(FileRequest request) throws Exception;

    InputStream getFileAsStream(FileRequest request) throws Exception;

    void downloadFile(FileRequest request, String localPath) throws Exception;

    List<String> getFolderFileNames(FileRequest request) throws Exception;


    void copyFile(FileCopyRequest request) throws Exception;


    long getFileSize(FileRequest request) throws Exception;
}
