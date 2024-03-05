package cn.master.yukio.service.impl;

import cn.master.yukio.file.FileCenter;
import cn.master.yukio.file.FileRequest;
import cn.master.yukio.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@Service
public class FileServiceImpl implements FileService {


    @Override
    public String upload(MultipartFile file, FileRequest request) throws Exception {
        return FileCenter.getRepository(request.getStorage()).saveFile(file, request);
    }

    @Override
    public String upload(InputStream inputStream, FileRequest request) throws Exception {
        return FileCenter.getRepository(request.getStorage()).saveFile(inputStream, request);
    }

    @Override
    public String upload(byte[] file, FileRequest request) throws Exception {
        return FileCenter.getRepository(request.getStorage()).saveFile(file, request);
    }

    @Override
    public byte[] download(FileRequest request) throws Exception {
        return FileCenter.getRepository(request.getStorage()).getFile(request);
    }

    @Override
    public void deleteFile(FileRequest request) throws Exception {
        FileCenter.getRepository(request.getStorage()).delete(request);
    }
}
