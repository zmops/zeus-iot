package com.zmops.iot.async.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * 抽象有向图
 *
 * @author create by TcSnZh on 2021/5/13-上午11:37
 */
public abstract class AbstractDirectedGraph<N, R> implements DirectedGraph<N, R> {

    @Override
    public String toString() {
        Set<N>                           nv   = nodesView();
        Set<? extends Graph.Entry<N, R>> rSet = getRelations();
        StringBuilder sb = new StringBuilder(nv.size() * 10 + rSet.size() * 20)
                .append(this.getClass().getSimpleName()).append("{nodes=[");
        Iterator<N> nit = nodesView().iterator();
        if (nit.hasNext()) {
            for (; ; ) {
                sb.append(nit.next());
                if (!nit.hasNext()) {
                    break;
                }
                sb.append(", ");
            }
        }
        sb.append("], relations=[");
        Iterator<? extends Graph.Entry<N, R>> eit = rSet.iterator();
        if (eit.hasNext()) {
            for (; ; ) {
                sb.append(eit.next());
                if (!eit.hasNext()) {
                    break;
                }
                sb.append(", ");
            }
        }
        return sb.append("]}").toString();
    }

    public abstract class AbstractNodesView extends AbstractSet<N> {
        @Override
        public boolean add(N n) {
            return AbstractDirectedGraph.this.addNode(n);
        }

        @Override
        public boolean remove(Object o) {
            N o1;
            //noinspection unchecked
            if (!AbstractDirectedGraph.this.containsNode(o1 = (N) o)) {
                return false;
            }
            AbstractDirectedGraph.this.removeNode(o1);
            return true;
        }
    }

    public static abstract class AbstractEntry<N, R> implements Graph.Entry<N, R> {
        @Override
        public int hashCode() {
            return this.getFrom().hashCode() ^ this.getTo().hashCode() ^ this.getRelation().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Graph.Entry)) {
                return false;
            }
            Graph.Entry obj1 = (Graph.Entry) obj;
            return Objects.equals(this.getFrom(), obj1.getFrom())
                    && Objects.equals(this.getTo(), obj1.getTo())
                    && Objects.equals(this.getRelation(), obj1.getRelation());
        }

        @Override
        public String toString() {
            return "{from=" + getFrom() + ", relation=" + getRelation() + ", to=" + getTo() + "]";
        }
    }
}
