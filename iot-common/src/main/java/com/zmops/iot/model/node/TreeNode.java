package com.zmops.iot.model.node;

import lombok.Data;

import java.util.List;

@Data
public class TreeNode implements Tree {

    /**
     * 根节点Id
     */
    public static final String ROOT_NODE_ID = "-1";

    /**
     * 根节点名称
     */
    public static final String ROOT_NODE_NAME = "根结点";


    /**
     * 节点id
     */
    private String id;

    /**
     * 父节点id
     */
    private String pId;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 是否打开节点
     */
    private Boolean open;

    /**
     * 是否被选中
     */
    private Boolean checked = false;

    /**
     * 是否是菜单(Y:是菜单  N:不是菜单)
     */
    private String isMenu;

    /**
     * 子节点
     */
    private List<TreeNode> children;

    @Override
    public String getNodeId() {
        return id.toString();
    }

    @Override
    public String getNodeParentId() {
        return pId.toString();
    }

    @Override
    public void setChildrenNodes(List childrenNodes) {
        this.children = childrenNodes;
    }

    /**
     * 创建根节点
     */
    public static TreeNode createRoot() {
        TreeNode root = new TreeNode();
        root.setChecked(false);
        root.setId(ROOT_NODE_ID);
        root.setName(ROOT_NODE_NAME);
        root.setOpen(true);
        root.setPId(null);
        return root;
    }
}
