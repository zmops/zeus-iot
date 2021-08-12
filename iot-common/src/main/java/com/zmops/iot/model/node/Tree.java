package com.zmops.iot.model.node;

import java.util.List;

public interface Tree {

    /**
     * 获取节点id
     */
    String getNodeId();

    /**
     * 获取节点父id
     */
    String getNodeParentId();

    /**
     * 获取子节点
     */
    List<TreeNode> getChildrenNodes();

    /**
     * 设置children
     */
    void setChildrenNodes(List childrenNodes);
}
