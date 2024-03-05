package cn.master.yukio.service;

import cn.master.yukio.dto.functional.FunctionalCaseAddRequest;
import com.mybatisflex.core.service.IService;
import cn.master.yukio.entity.FunctionalCase;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 功能用例 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IFunctionalCaseService extends IService<FunctionalCase> {

    FunctionalCase addFunctionalCase(FunctionalCaseAddRequest request, List<MultipartFile> files, String userId, String organizationId);
}
