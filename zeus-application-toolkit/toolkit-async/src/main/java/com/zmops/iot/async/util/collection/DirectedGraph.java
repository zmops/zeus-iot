package com.zmops.iot.async.util.collection;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @author create by TcSnZh on 2021/5/16-下午11:27
 */
public interface DirectedGraph<N, R> extends Graph<N, R> {
    @Override
    default boolean isDirected() {
        return true;
    }

    static <N, R> DirectedGraph<N, R> readOnlyDigraph(DirectedGraph<N, R> source) {
        return new ReadOnlyDirectedGraph<>(source);
    }

    static <N, R> DirectedGraph<N, R> synchronizedDigraph(DirectedGraph<N, R> source) {
        return synchronizedDigraph(source, new Object());
    }

    static <N, R> DirectedGraph<N, R> synchronizedDigraph(DirectedGraph<N, R> source, Object mutex) {
        return new SyncDirectedGraph<>(source, mutex);
    }

    class ReadOnlyDirectedGraph<N, R> extends AbstractDirectedGraph<N, R> {
        private final DirectedGraph<N, R> source;

        public ReadOnlyDirectedGraph(DirectedGraph<N, R> source) {
            this.source = source;
        }

        private static UnsupportedOperationException readOnlyGraph() {
            return new UnsupportedOperationException("readOnly graph");
        }

        @Override
        public boolean addNode(N node) {
            throw readOnlyGraph();
        }

        @Override
        public boolean containsNode(N node) {
            return source.containsNode(node);
        }

        @Override
        public Set<? extends Entry<N, R>> removeNode(N node) {
            throw readOnlyGraph();
        }

        @Override
        public R putRelation(N fromNode, R relation, N toNode) {
            throw readOnlyGraph();
        }

        @Override
        public Set<? extends Entry<N, R>> getRelationFrom(N from) {
            return source.getRelationFrom(from);
        }

        @Override
        public Set<? extends Entry<N, R>> getRelationTo(N to) {
            return source.getRelationTo(to);
        }

        @Override
        public Set<N> nodesView() {
            return new AbstractSet<N>() {
                private final Set<N> nodesViewSource = source.nodesView();

                @Override
                public Iterator<N> iterator() {
                    return new Iterator<N>() {
                        private final Iterator<N> iteratorSource = nodesViewSource.iterator();

                        @Override
                        public boolean hasNext() {
                            return iteratorSource.hasNext();
                        }

                        @Override
                        public N next() {
                            return iteratorSource.next();
                        }

                        @Override
                        public void remove() {
                            throw readOnlyGraph();
                        }
                    };
                }

                @Override
                public int size() {
                    return nodesViewSource.size();
                }

                @Override
                public boolean add(N n) {
                    throw readOnlyGraph();
                }

                @Override
                public boolean remove(Object o) {
                    throw readOnlyGraph();
                }
            };
        }

        @Override
        public Set<? extends Entry<N, R>> getRelations() {
            return source.getRelations();
        }
    }

    class SyncDirectedGraph<N, R> extends AbstractDirectedGraph<N, R> {
        private final DirectedGraph<N, R> source;
        private final Object mutex;

        public SyncDirectedGraph(DirectedGraph<N, R> source, Object mutex) {
            this.source = source;
            this.mutex = mutex;
        }

        @Override
        public boolean addNode(N node) {
            synchronized (mutex) {
                return source.addNode(node);
            }
        }

        @Override
        public boolean containsNode(N node) {
            synchronized (mutex) {
                return source.containsNode(node);
            }
        }

        @Override
        public Set<? extends Entry<N, R>> removeNode(N node) {
            synchronized (mutex) {
                return source.removeNode(node);
            }
        }

        @Override
        public R putRelation(N fromNode, R relation, N toNode) {
            synchronized (mutex) {
                return source.putRelation(fromNode, relation, toNode);
            }
        }

        @Override
        public Set<? extends Entry<N, R>> getRelationFrom(N from) {
            synchronized (mutex) {
                return source.getRelationFrom(from);
            }
        }

        @Override
        public Set<? extends Entry<N, R>> getRelationTo(N to) {
            synchronized (mutex) {
                return source.getRelationTo(to);
            }
        }

        @Override
        public Set<N> nodesView() {
            synchronized (mutex) {
                return Collections.synchronizedSet(source.nodesView());
            }
        }

        @Override
        public Set<? extends Entry<N, R>> getRelations() {
            synchronized (mutex) {
                return source.getRelations();
            }
        }
    }
}
