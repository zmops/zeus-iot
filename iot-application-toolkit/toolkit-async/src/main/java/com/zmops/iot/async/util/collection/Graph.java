package com.zmops.iot.async.util.collection;

import java.util.Set;

/**
 * 图数据结构
 *
 * @author create by TcSnZh on 2021/5/13-上午11:37
 */
@SuppressWarnings("unused")
public interface Graph<N, R> {
    /**
     * 添加节点。
     * 如果节点已经存在，则不会添加。
     *
     * @param node 添加进图的节点
     * @return 添加成功返回true，如果节点已经存在返回false。
     */
    boolean addNode(N node);

    /**
     * 添加一堆Node，任一成功返回true
     */
    default boolean addNode(N... nodes) {
        boolean success = false;
        for (N node : nodes) {
            if (addNode(node)) {
                success = true;
            }
        }
        return success;
    }

    /**
     * 是否存在节点
     *
     * @param node 节点。
     * @return 存在返回true，否则返回false。
     */
    boolean containsNode(N node);

    /**
     * 移除节点。
     * 返回与该节点有关系的，被一并移出的键。
     *
     * @param node 节点
     * @return 返回值不会为null。
     * @throws IllegalArgumentException 如果两个节点任一不存在本图中，抛出异常。
     */
    Set<? extends Entry<N, R>> removeNode(N node);

    /**
     * 添加关系
     * 在无向图中fromNode与toNode参数的位置调换没有影响。
     *
     * @param fromNode 从这个节点开始
     * @param relation 关系
     * @param toNode   以那个节点为目标
     * @return 如果之前存在关系，则会替换之前的关系，返回出被替换的之前存在的关系。如果之前没有关系，返回null。
     * @throws IllegalArgumentException 如果两个节点任一不存在本图中，抛出该异常。
     */
    R putRelation(N fromNode, R relation, N toNode);

    /**
     * 获取“从这个节点开始”的所有关系
     *
     * @param from 关系开始的节点
     * @return 返回 {@link Entry}键。
     */
    Set<? extends Entry<N, R>> getRelationFrom(N from);

    /**
     * 获取“以这个节点为目标”的所有关系
     *
     * @param to 被关系的节点
     * @return 返回 {@link Entry}键。
     */
    Set<? extends Entry<N, R>> getRelationTo(N to);

    /**
     * 返回全部节点视图
     *
     * @return 视图
     */
    Set<N> nodesView();

    /**
     * 返回全部关系，返回的是新Set
     *
     * @return 与本类无关的Set
     */
    Set<? extends Entry<N, R>> getRelations();

    /**
     * 是否有向
     */
    boolean isDirected();

    interface Entry<N, R> {
        N getFrom();

        N getTo();

        R getRelation();
    }
}
