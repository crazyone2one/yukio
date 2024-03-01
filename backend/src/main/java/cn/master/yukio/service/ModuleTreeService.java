package cn.master.yukio.service;

import cn.master.yukio.constants.ModuleConstants;
import cn.master.yukio.dto.BaseTreeNode;
import cn.master.yukio.util.NodeSortUtils;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
public abstract class ModuleTreeService {
    protected static final long LIMIT_POS = NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;

    public BaseTreeNode getDefaultModule(String name) {
        //默认模块下不允许创建子模块。  它本身也就是叶子节点。
        return new BaseTreeNode(ModuleConstants.DEFAULT_NODE_ID, name, ModuleConstants.NODE_TYPE_DEFAULT, ModuleConstants.ROOT_NODE_PARENT_ID);
    }

}
