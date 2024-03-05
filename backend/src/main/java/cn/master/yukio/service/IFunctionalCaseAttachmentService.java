package cn.master.yukio.service;

import cn.master.yukio.entity.FunctionalCaseAttachment;
import com.mybatisflex.core.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 功能用例和附件的中间表 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IFunctionalCaseAttachmentService extends IService<FunctionalCaseAttachment> {

    List<String> uploadFile(String projectId, String caseId, List<MultipartFile> files, Boolean isLocal, String userId);

    FunctionalCaseAttachment creatAttachment(String fileId, String fileName, long fileSize, String caseId, Boolean isLocal, String userId);

    void association(List<String> relateFileMetaIds, String caseId, String userId, String logUrl, String projectId);
}
