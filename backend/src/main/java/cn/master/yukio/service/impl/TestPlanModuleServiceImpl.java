package cn.master.yukio.service.impl;

import cn.master.yukio.constants.ModuleConstants;
import cn.master.yukio.dto.NodeMoveRequest;
import cn.master.yukio.dto.plan.TestPlanBatchProcessRequest;
import cn.master.yukio.dto.plan.TestPlanModuleCreateRequest;
import cn.master.yukio.dto.plan.TestPlanModuleUpdateRequest;
import cn.master.yukio.entity.TestPlanModule;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.TestPlanModuleMapper;
import cn.master.yukio.service.ITestPlanModuleService;
import cn.master.yukio.service.ITestPlanService;
import cn.master.yukio.service.TestPlanModuleLogService;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.master.yukio.entity.table.TestPlanModuleTableDef.TEST_PLAN_MODULE;

/**
 * 测试计划模块 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestPlanModuleServiceImpl extends ServiceImpl<TestPlanModuleMapper, TestPlanModule> implements ITestPlanModuleService {
    protected static final long LIMIT_POS = 64;
    private final TestPlanModuleLogService testPlanModuleLogService;
    private final ITestPlanService testPlanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(TestPlanModuleCreateRequest request, String operator, String requestUrl, String requestMethod) {
        TestPlanModule testPlanModule = TestPlanModule.builder()
                .name(request.getName())
                .parentId(request.getParentId())
                .projectId(request.getProjectId())
                .pos(countPos(request.getParentId()))
                .createUser(operator)
                .updateUser(operator)
                .build();
        checkDataValidity(testPlanModule);
        mapper.insert(testPlanModule);
        testPlanModuleLogService.saveAddLog(testPlanModule, operator, requestUrl, requestMethod);
        return testPlanModule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TestPlanModuleUpdateRequest request, String userId, String requestUrl, String requestMethod) {
        TestPlanModule module = mapper.selectOneById(request.getId());
        TestPlanModule updateModule = new TestPlanModule();
        updateModule.setId(request.getId());
        updateModule.setName(request.getName().trim());
        updateModule.setParentId(module.getParentId());
        this.checkDataValidity(updateModule);
        updateModule.setUpdateUser(userId);
        mapper.update(updateModule);
        TestPlanModule newModule = mapper.selectOneById(request.getId());
        testPlanModuleLogService.saveUpdateLog(module, newModule, module.getProjectId(), userId, requestUrl, requestMethod);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(String deleteId, String operator, String requestUrl, String requestMethod) {
        TestPlanModule deleteModule = mapper.selectOneById(deleteId);
        if (Objects.nonNull(deleteModule)) {
            deleteModule(Collections.singletonList(deleteId), deleteModule.getProjectId(), operator, requestUrl, requestMethod);
            //记录日志
            testPlanModuleLogService.saveDeleteLog(deleteModule, operator, requestUrl, requestMethod);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveNode(NodeMoveRequest request, String currentUser, String requestUrl, String requestMethod) {
        // todo
    }

    @Override
    public List<TestPlanModule> getTree(String projectId) {
        List<TestPlanModule> fileModuleList = queryChain().select(TEST_PLAN_MODULE.ID, TEST_PLAN_MODULE.NAME, TEST_PLAN_MODULE.PARENT_ID)
                .where(TEST_PLAN_MODULE.PROJECT_ID.eq(projectId)).list();
        return buildTreeAndCountResource(fileModuleList, true, Translator.get("default.module"));
    }

    @Override
    public List<TestPlanModule> buildTreeAndCountResource(List<TestPlanModule> traverseList, boolean haveVirtualRootNode, String virtualRootName) {
        List<TestPlanModule> baseTreeNodeList = new ArrayList<>();
        if (haveVirtualRootNode) {
            TestPlanModule defaultNode = new TestPlanModule();
            defaultNode.setId(ModuleConstants.DEFAULT_NODE_ID);
            defaultNode.setName(virtualRootName);
            defaultNode.setType(ModuleConstants.NODE_TYPE_DEFAULT);
            defaultNode.setParentId(ModuleConstants.ROOT_NODE_PARENT_ID);
            defaultNode.genModulePath(null);
            baseTreeNodeList.add(defaultNode);
        }
        return baseTreeNodeList;
    }

    private void deleteModule(List<String> deleteIds, String projectId, String operator, String requestUrl, String requestMethod) {
        if (CollectionUtils.isEmpty(deleteIds)) {
            return;
        }
        mapper.deleteBatchByIds(deleteIds);
        TestPlanBatchProcessRequest request = new TestPlanBatchProcessRequest();
        request.setModuleIds(deleteIds);
        request.setSelectAll(true);
        request.setProjectId(projectId);
        testPlanService.batchDelete(request, operator, requestUrl, requestMethod);
        List<String> childrenIds = queryChain().select(TEST_PLAN_MODULE.ID).where(TestPlanModule::getParentId).in(deleteIds).listAs(String.class);
        if (CollectionUtils.isNotEmpty(childrenIds)) {
            deleteModule(childrenIds, projectId, operator, requestUrl, requestMethod);
        }
    }

    private void checkDataValidity(TestPlanModule module) {
        if (!StringUtils.equalsIgnoreCase(module.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            //检查父ID是否存在
            long count = queryChain().where(TestPlanModule::getId).eq(module.getParentId()).count();
            if (count == 0) {
                throw new MSException(Translator.get("parent.node.not_blank"));
            }
            queryChain().clear();
            if (StringUtils.isNotBlank(module.getProjectId())) {
                //检查项目ID是否和父节点ID一致
                count = queryChain().where(TestPlanModule::getId).eq(module.getParentId()).and(TestPlanModule::getProjectId).eq(module.getProjectId()).count();
                if (count == 0) {
                    throw new MSException(Translator.get("project.cannot.match.parent"));
                }
                queryChain().clear();
            }
        }
        long count = queryChain().where(TestPlanModule::getParentId).eq(module.getParentId())
                .and(TestPlanModule::getName).eq(module.getName()).and(TestPlanModule::getId).ne(module.getId()).count();
        if (count > 0) {
            throw new MSException(Translator.get("node.name.repeat"));
        }
        queryChain().clear();
    }

    protected Long countPos(String parentId) {
        Long maxPos = queryChain().select(QueryMethods.max(TestPlanModule::getPos)).where(TestPlanModule::getParentId).eq(parentId).oneAs(Long.class);
        if (maxPos == null) {
            return LIMIT_POS;
        } else {
            return maxPos + LIMIT_POS;
        }
    }
}
