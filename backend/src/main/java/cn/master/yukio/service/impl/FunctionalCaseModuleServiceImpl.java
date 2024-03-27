package cn.master.yukio.service.impl;

import cn.master.yukio.constants.HttpMethodConstants;
import cn.master.yukio.constants.ModuleConstants;
import cn.master.yukio.constants.OperationLogModule;
import cn.master.yukio.constants.OperationLogType;
import cn.master.yukio.dto.BaseTreeNode;
import cn.master.yukio.dto.LogDTO;
import cn.master.yukio.dto.functional.FunctionalCaseModuleCreateRequest;
import cn.master.yukio.dto.functional.FunctionalCaseModuleUpdateRequest;
import cn.master.yukio.entity.FunctionalCase;
import cn.master.yukio.entity.FunctionalCaseModule;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.FunctionalCaseMapper;
import cn.master.yukio.mapper.FunctionalCaseModuleMapper;
import cn.master.yukio.service.IFunctionalCaseModuleService;
import cn.master.yukio.service.IOperationLogService;
import cn.master.yukio.util.JsonUtils;
import cn.master.yukio.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cn.master.yukio.entity.table.FunctionalCaseModuleTableDef.FUNCTIONAL_CASE_MODULE;
import static cn.master.yukio.entity.table.FunctionalCaseTableDef.FUNCTIONAL_CASE;
import static cn.master.yukio.service.impl.TestPlanModuleServiceImpl.LIMIT_POS;

/**
 * 功能用例模块 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class FunctionalCaseModuleServiceImpl extends ServiceImpl<FunctionalCaseModuleMapper, FunctionalCaseModule> implements IFunctionalCaseModuleService {
    private final FunctionalCaseMapper functionalCaseMapper;
    private final IOperationLogService operationLogService;

    @Override
    public String add(FunctionalCaseModuleCreateRequest request, String userId) {
        FunctionalCaseModule functionalCaseModule = FunctionalCaseModule.builder()
                .name(request.getName())
                .parentId(request.getParentId())
                .projectId(request.getProjectId())
                .build();
        checkDataValidity(functionalCaseModule);
        functionalCaseModule.setCreateUser(userId);
        functionalCaseModule.setUpdateUser(userId);
        functionalCaseModule.setPos(this.countPos(request.getParentId()));
        mapper.insert(functionalCaseModule);
        return functionalCaseModule.getId();
    }

    @Override
    public void update(FunctionalCaseModuleUpdateRequest request, String userId) {
        FunctionalCaseModule updateModule = mapper.selectOneById(request.getId());
        if (Objects.isNull(updateModule)) {
            throw new MSException(Translator.get("case_module.not.exist"));
        }
        updateModule.setName(request.getName());
        this.checkDataValidity(updateModule);
        updateModule.setUpdateUser(userId);
        mapper.update(updateModule);
    }

    @Override
    public void deleteModule(String id) {
        FunctionalCaseModule deleteModule = mapper.selectOneById(id);
        if (Objects.nonNull(deleteModule)) {
            List<FunctionalCase> functionalCases = deleteModuleByIds(Collections.singletonList(id), new ArrayList<>());
            batchDelLog(functionalCases, deleteModule.getProjectId());
        }
    }

    @Override
    public List<FunctionalCaseModule> getTree(String projectId) {
        //设置递归查询深度为 10 层
        RelationManager.setMaxDepth(10);
        QueryChain<FunctionalCaseModule> qw = queryChain().where(FUNCTIONAL_CASE_MODULE.PROJECT_ID.eq(projectId)).orderBy(FUNCTIONAL_CASE_MODULE.POS.desc());
        List<FunctionalCaseModule> functionalModuleList = mapper.selectListWithRelationsByQuery(qw);
        return buildTreeAndCountResource(functionalModuleList, true, Translator.get("functional_case.module.default.name"));
    }

    private List<FunctionalCaseModule> buildTreeAndCountResource(List<FunctionalCaseModule> traverseList, boolean haveVirtualRootNode, String virtualRootName) {
        List<FunctionalCaseModule> baseTreeNodeList = new ArrayList<>();
        if (haveVirtualRootNode) {
            FunctionalCaseModule defaultNode = new FunctionalCaseModule();
            defaultNode.setId(ModuleConstants.DEFAULT_NODE_ID);
            defaultNode.setName(virtualRootName);
            defaultNode.setType(ModuleConstants.NODE_TYPE_DEFAULT);
            defaultNode.setParentId(ModuleConstants.ROOT_NODE_PARENT_ID);
            defaultNode.genModulePath(null);
            baseTreeNodeList.add(defaultNode);
        }
        int lastSize = 0;
        Map<String, FunctionalCaseModule> baseTreeNodeMap = new HashMap<>();
        while (CollectionUtils.isNotEmpty(traverseList) && traverseList.size() != lastSize) {
            lastSize = traverseList.size();
            List<FunctionalCaseModule> notMatchedList = new ArrayList<>();
            for (FunctionalCaseModule treeNode : traverseList) {
                if (!baseTreeNodeMap.containsKey(treeNode.getParentId()) && !StringUtils.equalsIgnoreCase(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    notMatchedList.add(treeNode);
                    continue;
                }
                FunctionalCaseModule node = new FunctionalCaseModule();
                BeanUtils.copyProperties(treeNode, node);
                node.genModulePath(baseTreeNodeMap.get(treeNode.getParentId()));
                baseTreeNodeMap.put(treeNode.getId(), node);
                if (StringUtils.equalsIgnoreCase(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    baseTreeNodeList.add(node);
                } else if (baseTreeNodeMap.containsKey(treeNode.getParentId())) {
                    baseTreeNodeMap.get(treeNode.getParentId()).addChild(node);
                }
            }
            traverseList = notMatchedList;
        }
        return baseTreeNodeList;
    }

    private void batchDelLog(List<FunctionalCase> functionalCases, String projectId) {
        List<LogDTO> dtoList = new ArrayList<>();
        functionalCases.forEach(item -> {
            LogDTO dto = new LogDTO(
                    projectId,
                    "",
                    item.getId(),
                    item.getCreateUser(),
                    OperationLogType.DELETE.name(),
                    OperationLogModule.FUNCTIONAL_CASE,
                    item.getName());

            dto.setPath("/functional/case/module/delete/");
            dto.setMethod(HttpMethodConstants.GET.name());
            dto.setOriginalValue(JsonUtils.toJsonByte(item));
            dtoList.add(dto);
        });
        operationLogService.batchAdd(dtoList);
    }

    private List<FunctionalCase> deleteModuleByIds(List<String> deleteIds, List<FunctionalCase> functionalCases) {
        if (CollectionUtils.isEmpty(deleteIds)) {
            return functionalCases;
        }
        mapper.deleteBatchByIds(deleteIds);
        QueryChain<FunctionalCase> qw = QueryChain.of(FunctionalCase.class).where(FUNCTIONAL_CASE.MODULE_ID.in(deleteIds));
        List<FunctionalCase> functionalCaseList = functionalCaseMapper.selectListByQuery(qw);
        if (CollectionUtils.isNotEmpty(functionalCaseList)) {
            functionalCases.addAll(functionalCaseList);
        }
        UpdateChain.of(FunctionalCase.class)
                .set(FunctionalCase::getDeleted, true)
                .set(FunctionalCase::getModuleId, "root")
                .where(FunctionalCase::getModuleId).in(deleteIds).update();
        List<String> childrenIds = queryChain().select(FUNCTIONAL_CASE_MODULE.ID).where(FUNCTIONAL_CASE_MODULE.PARENT_ID.in(deleteIds)).listAs(String.class);
        if (CollectionUtils.isNotEmpty(childrenIds)) {
            deleteModuleByIds(childrenIds, functionalCases);
        }
        return functionalCases;
    }

    private Long countPos(String parentId) {
        Long maxPos = queryChain().select(QueryMethods.max(FunctionalCaseModule::getPos)).where(FunctionalCaseModule::getParentId).eq(parentId).oneAs(Long.class);
        if (maxPos == null) {
            return LIMIT_POS;
        } else {
            return maxPos + LIMIT_POS;
        }
    }

    void checkDataValidity(FunctionalCaseModule functionalCaseModule) {
        if (!StringUtils.equals(functionalCaseModule.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            //检查父ID是否存在
            boolean exists = queryChain().where(FUNCTIONAL_CASE_MODULE.ID.eq(functionalCaseModule.getParentId())).exists();
            if (!exists) {
                throw new MSException(Translator.get("parent.node.not_blank"));
            }
            queryChain().clear();
            //检查项目ID是否和父节点ID一致
            boolean exists1 = queryChain().where(FUNCTIONAL_CASE_MODULE.PROJECT_ID.eq(functionalCaseModule.getProjectId())
                    .and(FUNCTIONAL_CASE_MODULE.ID.eq(functionalCaseModule.getParentId()))).exists();
            if (!exists1) {
                throw new MSException(Translator.get("project.cannot.match.parent"));
            }
            queryChain().clear();
            boolean nameRepeat = queryChain().where(FUNCTIONAL_CASE_MODULE.PARENT_ID.eq(functionalCaseModule.getParentId())
                    .and(FUNCTIONAL_CASE_MODULE.ID.ne(functionalCaseModule.getParentId()))
                    .and(FUNCTIONAL_CASE_MODULE.NAME.eq(functionalCaseModule.getName()))
                    .and(FUNCTIONAL_CASE_MODULE.PROJECT_ID.eq(functionalCaseModule.getProjectId()))
            ).exists();
            if (nameRepeat) {
                throw new MSException(Translator.get("node.name.repeat"));
            }
            queryChain().clear();
        }
    }
}
