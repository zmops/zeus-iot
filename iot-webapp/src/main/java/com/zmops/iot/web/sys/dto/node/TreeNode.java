package com.zmops.iot.web.sys.dto.node;



import com.zmops.iot.model.node.Tree;
import com.zmops.iot.util.ToolUtil;
import lombok.Data;

import java.util.List;

/**
 * 字典信息
 *
 * @author nantian
 * @Date 2018/12/8 18:16
 */
@Data
public class TreeNode implements Tree {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 父部门id
     */
    private Long pId;
    /**
     * 名称
     */
    private String name;

    /**
     * URL
     */
    private String url;

    /**
     * 是否菜单
     */
    private String menuFlag;

    /**
     * 是否已授权
     */
    private Boolean isChecked;

    /**
     * 子节点
     */
    private List<TreeNode> childrenNodes;

    @Override
    public String getNodeId() {
        if (ToolUtil.isNotEmpty(id)) {
            return String.valueOf(id);
        } else {
            return "0";
        }
    }

    @Override
    public String getNodeParentId() {
        if (ToolUtil.isNotEmpty(pId)) {
            return String.valueOf(pId);
        } else {
            return "0";
        }
    }

    @Override
    public void setChildrenNodes(List childrenNodes) {
        this.childrenNodes = childrenNodes;
    }
}
