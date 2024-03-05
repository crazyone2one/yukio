package cn.master.yukio.service.impl;

import cn.master.yukio.dto.project.FileLogRecord;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.util.Translator;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.FileAssociation;
import cn.master.yukio.mapper.FileAssociationMapper;
import cn.master.yukio.service.IFileAssociationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件资源关联 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class FileAssociationServiceImpl extends ServiceImpl<FileAssociationMapper, FileAssociation> implements IFileAssociationService {

    @Override
    public List<String> association(String sourceId, String sourceType, List<String> fileIds, FileLogRecord fileLogRecord) {
        if (CollectionUtils.isEmpty(fileIds)) {
            throw new MSException(Translator.get("file.not.exist"));
        }
        return null;
    }
}
