package com.zmops.iot.async.util.collection;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 线程不安全的有向图。
 * <p/>
 * 不允许放入null。
 *
 * @author create by TcSnZh on 2021/5/14-上午2:22
 */
public class CommonDirectedGraph<N, R> extends AbstractDirectedGraph<N, R> {

    // ========== properties ==========

    private final StoreArk<N> nodes = new CachedStoreArk<>();
    private final Array2D<R>  arr   = new SparseArray2D<>();

    // ========== methods ==========

    @Override
    public boolean addNode(N node) {
        if (containsNode(Objects.requireNonNull(node))) {
            return false;
        }
        nodes.store(node);
        return true;
    }

    @Override
    public boolean containsNode(N node) {
        return node != null && findNodeId(node, false) >= 0;
    }

    @Override
    public Set<? extends Graph.Entry<N, R>> removeNode(N node) {
        int                              id  = findNodeId(Objects.requireNonNull(node), true);
        LinkedHashSet<Graph.Entry<N, R>> res = new LinkedHashSet<>();
        // 查找node为from的键
        arr.fullLine(id).forEach((toNodeId, relation) -> {
            res.add(new OuterEntry<>(node, nodes.peek(toNodeId), relation));
            arr.remove(id, toNodeId);
        });
        // 查找node为to的键
        arr.fullColumn(id).forEach((fromNodeId, relation) -> {
            // 在上一次遍历中，fromNodeId为id，
            if (fromNodeId == id) {
                return;
            }
            res.add(new OuterEntry<>(nodes.peek(fromNodeId), node, relation));
            arr.remove(fromNodeId, id);
        });
        nodes.takeOut(id);
        return res;
    }

    @Override
    public R putRelation(N fromNode, R relation, N toNode) {
        return arr.add(
                findNodeId(Objects.requireNonNull(fromNode), true),
                findNodeId(Objects.requireNonNull(toNode), true),
                Objects.requireNonNull(relation)
        );
    }

    @Override
    public Set<? extends Graph.Entry<N, R>> getRelationFrom(N from) {
        int                              id  = findNodeId(Objects.requireNonNull(from), true);
        LinkedHashSet<Graph.Entry<N, R>> res = new LinkedHashSet<>();
        // 查找node为from的键
        arr.fullLine(id).forEach((toNodeId, relation) -> res.add(new OuterEntry<>(from, nodes.peek(toNodeId), relation)));
        return res;
    }

    @Override
    public Set<? extends Graph.Entry<N, R>> getRelationTo(N to) {
        int                              id  = findNodeId(Objects.requireNonNull(to), true);
        LinkedHashSet<Graph.Entry<N, R>> res = new LinkedHashSet<>();
        // 查找node为to的键
        arr.fullColumn(id).forEach((fromNodeId, relation) ->
                res.add(new OuterEntry<>(nodes.peek(fromNodeId), to, relation)));
        return res;
    }

    @Override
    public Set<N> nodesView() {
        return new AbstractNodesView() {
            @Override
            public Iterator<N> iterator() {
                return nodes.stream().map(Map.Entry::getValue).iterator();
            }

            @Override
            public int size() {
                return nodes.size();
            }
        };
    }

    @Override
    public Set<? extends Graph.Entry<N, R>> getRelations() {
        return arr.stream().map((Function<Array2D.Point<R>, Graph.Entry<N, R>>) rPoint -> new OuterEntry<>(
                nodes.peek(rPoint.getLine()),
                nodes.peek(rPoint.getColumn()),
                rPoint.getElement()
        )).collect(Collectors.toSet());
    }

    private int findNodeId(N node, boolean mustExistElseThrowEx) {
        int id = nodes.findId(Objects.requireNonNull(node));
        if (mustExistElseThrowEx && id < 0) {
            throw new IllegalArgumentException("No node exists : " + node);
        }
        return id;
    }

    private static class OuterEntry<N, R> extends AbstractDirectedGraph.AbstractEntry<N, R> {
        private final N from;
        private final N to;
        private final R relation;

        public OuterEntry(N from, N to, R relation) {
            this.from = from;
            this.to = to;
            this.relation = relation;
        }

        @Override
        public N getFrom() {
            return from;
        }

        @Override
        public N getTo() {
            return to;
        }

        @Override
        public R getRelation() {
            return relation;
        }
    }
}
