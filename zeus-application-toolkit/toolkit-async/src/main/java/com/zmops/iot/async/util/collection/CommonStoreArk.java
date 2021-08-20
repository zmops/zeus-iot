package com.zmops.iot.async.util.collection;

import java.util.*;

/**
 * 自动扩容的id储物柜，线程不安全。
 *
 * @author create by TcSnZh on 2021/5/13-下午1:24
 */
public class CommonStoreArk<E> extends AbstractStoreArk<E> {
    private Object[] elements;

    /**
     * 已经分配的下标数
     */
    private int allocSize = 0;

    /**
     * 保存着最小空元素的队列
     */
    private final Queue<Integer> emptyPoints = new PriorityQueue<>(Integer::compareTo);

    public CommonStoreArk(int initialCapacity) {
        elements = new Object[initialCapacity];
    }

    public CommonStoreArk() {
        this(10);
    }

    @Override
    public int store(E element) {
        int id;
        elements[id = pollId()] = element;
        return id;
    }

    @Override
    public E peek(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id " + id + " can't be negative");
        }
        if (id >= elements.length) {
            return null;
        }
        //noinspection unchecked
        return (E) elements[id];
    }

    @Override
    public E takeOut(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id " + id + " can't be negative");
        }
        if (id >= elements.length) {
            return null;
        }
        //noinspection unchecked
        E out = (E) elements[id];
        elements[id] = null;
        if (id == allocSize - 1) {
            allocSize--;
        } else {
            emptyPoints.add(id);
        }
        return out;
    }

    @Override
    public int size() {
        return allocSize - emptyPoints.size();
    }

    @Override
    public Iterator<Map.Entry<Integer, E>> iterator() {
        return new Iterator<Map.Entry<Integer, E>>() {
            private final Map.Entry<Integer, E>[] items;

            private int idx = 0;

            {
                //noinspection unchecked
                items = new Map.Entry[size()];
                int               itemsIdx      = 0;
                Iterator<Integer> emptyPointItr = emptyPoints.iterator();
                for (int i = 0; i < allocSize; i++) {
                    Object element = elements[i];
                    if (element == null) {
                        continue;
                    }
                    final int _i = i;
                    //noinspection unchecked
                    items[itemsIdx++] = new Map.Entry<Integer, E>() {
                        private final int k = _i;
                        private E v = (E) element;

                        @Override
                        public Integer getKey() {
                            return k;
                        }

                        @Override
                        public E getValue() {
                            return v;
                        }

                        @Override
                        public E setValue(E value) {
                            E _v = this.v;
                            this.v = value;
                            return _v;
                        }

                        @Override
                        public String toString() {
                            return "{" + k + ':' + v + '}';
                        }
                    };
                }
            }


            @Override
            public boolean hasNext() {
                return idx < items.length;
            }

            @Override
            public Map.Entry<Integer, E> next() {
                return items[idx++];
            }
        };
    }

    @Override
    public int findId(E element) {
        int i = 0;
        for (Object o : elements) {
            if (Objects.equals(o, element)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private int pollId() {
        if (!emptyPoints.isEmpty()) {
            return emptyPoints.poll();
        }
        int id     = allocSize++;
        int length = elements.length;
        if (id >= length) {
            // 扩容
            elements = Arrays.copyOf(elements, Math.max(length + 1, length + (length >> 1)));
        }
        return id;
    }
}
