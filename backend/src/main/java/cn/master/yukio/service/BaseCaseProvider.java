package cn.master.yukio.service;

import java.util.Map;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
public interface BaseCaseProvider {
    /**
     * 更新用例评审数据
     * @param paramMap 更新用例评审所需参数
     */
    void updateCaseReview(Map<String, Object> paramMap);
}
