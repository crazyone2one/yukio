package cn.master.yukio.service.impl;

import cn.master.yukio.constants.DefaultRepositoryDir;
import cn.master.yukio.constants.StorageType;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.file.FileRequest;
import cn.master.yukio.service.FileService;
import cn.master.yukio.util.Translator;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.FunctionalCaseAttachment;
import cn.master.yukio.mapper.FunctionalCaseAttachmentMapper;
import cn.master.yukio.service.IFunctionalCaseAttachmentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 功能用例和附件的中间表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class FunctionalCaseAttachmentServiceImpl extends ServiceImpl<FunctionalCaseAttachmentMapper, FunctionalCaseAttachment> implements IFunctionalCaseAttachmentService {
    private final FileService fileService;

    @Value("50MB")
    private DataSize maxFileSize;

    @Override
    public List<String> uploadFile(String projectId, String caseId, List<MultipartFile> files, Boolean isLocal, String userId) {
        List<String> fileIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(files)) {
            files.forEach(file -> {
                String fileId = UUID.randomUUID().toString();
                FileRequest fileRequest = new FileRequest();
                fileRequest.setFileName(file.getOriginalFilename());
                fileRequest.setFolder(DefaultRepositoryDir.getFunctionalCaseDir(projectId, caseId) + "/" + fileId);
                fileRequest.setStorage(StorageType.LOCAL.name());
                if (file.getSize() > maxFileSize.toBytes()) {
                    throw new MSException(Translator.get("file.size.is.too.large"));
                }
                try {
                    fileService.upload(file, fileRequest);
                }catch (Exception e) {
                    throw new MSException("save file error");
                }
                saveCaseAttachment(fileId, file, caseId, isLocal, userId);
                fileIds.add(fileId);
            });
        }
        return fileIds;
    }

    @Override
    public FunctionalCaseAttachment creatAttachment(String fileId, String fileName, long fileSize, String caseId, Boolean isLocal, String userId) {
        return FunctionalCaseAttachment.builder()
                .caseId(caseId)
                .fileName(fileName)
                .fileId(fileId)
                .size(fileSize)
                .local(isLocal)
                .createUser(userId)
                .build();
    }

    @Override
    public void association(List<String> relateFileMetaIds, String caseId, String userId, String logUrl, String projectId) {

    }

    private void saveCaseAttachment(String fileId, MultipartFile file, String caseId, Boolean isLocal, String userId) {
        FunctionalCaseAttachment caseAttachment = creatAttachment(fileId, file.getOriginalFilename(), file.getSize(), caseId, isLocal, userId);
        mapper.insert(caseAttachment);
    }
}
