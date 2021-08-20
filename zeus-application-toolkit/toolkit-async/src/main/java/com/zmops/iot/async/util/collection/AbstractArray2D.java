package com.zmops.iot.async.util.collection;

import com.zmops.iot.async.util.BiInt;

import java.util.Iterator;

/**
 * @author create by TcSnZh on 2021/5/14-下午9:51
 */
public abstract class AbstractArray2D<E> implements Array2D<E> {
    /**
     * 用于代替null
     */
    protected static final Object NULL = new Object() {
        @Override
        public String toString() {
            return "null";
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @SuppressWarnings("EqualsDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            //noinspection ConstantConditions
            return obj == null || obj == NULL || obj.equals(null);
        }
    };

    @Override
    public String toString() {
        StringBuilder      sb = new StringBuilder(64).append(this.getClass().getSimpleName()).append('{');
        Iterator<Point<E>> it = iterator();
        if (it.hasNext()) {
            while (true) {
                Point<E> point = it.next();
                sb.append('{').append(point.getIdx()).append(':').append(point.getElement()).append('}');
                if (!it.hasNext()) {
                    break;
                }
                sb.append(", ");
            }
        }
        return sb.append('}').toString();
    }

    public static class PointImpl<E> implements Point<E> {
        private final BiInt idx;
        private final E     element;

        public PointImpl(BiInt idx, E element) {
            this.idx = idx;
            this.element = element;
        }

        @Override
        public BiInt getIdx() {
            return idx;
        }

        @Override
        public E getElement() {
            return element;
        }

        @Override
        public String toString() {
            return "{" + idx + ":" + element + "}";
        }
    }
}
