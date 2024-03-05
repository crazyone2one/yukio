package cn.master.yukio.service;

import cn.master.yukio.dto.project.FileLogRecord;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.FileAssociation;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 文件资源关联 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IFileAssociationService extends IService<FileAssociation> {
    List<String> association(String sourceId, String sourceType, List<String> fileIds, @Validated FileLogRecord fileLogRecord);

}
