package cn.master.yukio.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@Component
public class MinioRepository implements FileRepository{
    @Override
    public String saveFile(MultipartFile file, FileRequest request) throws Exception {
        return null;
    }

    @Override
    public String saveFile(byte[] bytes, FileRequest request) throws Exception {
        return null;
    }

    @Override
    public String saveFile(InputStream inputStream, FileRequest request) throws Exception {
        return null;
    }

    @Override
    public void delete(FileRequest request) throws Exception {

    }

    @Override
    public void deleteFolder(FileRequest request) throws Exception {

    }

    @Override
    public byte[] getFile(FileRequest request) throws Exception {
        return new byte[0];
    }

    @Override
    public InputStream getFileAsStream(FileRequest request) throws Exception {
        return null;
    }

    @Override
    public void downloadFile(FileRequest request, String localPath) throws Exception {

    }

    @Override
    public List<String> getFolderFileNames(FileRequest request) throws Exception {
        return null;
    }

    @Override
    public void copyFile(FileCopyRequest request) throws Exception {

    }

    @Override
    public long getFileSize(FileRequest request) throws Exception {
        return 0;
    }
}
