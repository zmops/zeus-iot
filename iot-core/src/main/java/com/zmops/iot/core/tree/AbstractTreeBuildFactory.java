package com.zmops.iot.core.tree;

import java.util.List;

/**
 * 树构建的抽象类，定义构建tree的基本步骤
 *
 * @author nantian
 * @Date 2018/7/25 下午5:59
 */
public abstract class AbstractTreeBuildFactory<T> {

    /**
     * 树节点构建整体过程
     *
     * @author nantian
     * @Date 2018/7/26 上午9:45
     */
    public List<T> doTreeBuild(List<T> nodes) {

        //构建之前的节点处理工作
        List<T> readyToBuild = beforeBuild(nodes);

        //具体构建的过程
        List<T> builded = executeBuilding(readyToBuild);

        //构建之后的处理工作
        return afterBuild(builded);
    }

    /**
     * 构建之前的处理工作
     *
     * @author nantian
     * @Date 2018/7/26 上午10:10
     */
    protected abstract List<T> beforeBuild(List<T> nodes);

    /**
     * 具体的构建过程
     *
     * @author nantian
     * @Date 2018/7/26 上午10:11
     */
    protected abstract List<T> executeBuilding(List<T> nodes);

    /**
     * 构建之后的处理工作
     *
     * @author nantian
     * @Date 2018/7/26 上午10:11
     */
    protected abstract List<T> afterBuild(List<T> nodes);
}
