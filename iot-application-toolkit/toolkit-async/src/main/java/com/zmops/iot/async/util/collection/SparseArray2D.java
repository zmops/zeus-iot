package com.zmops.iot.async.util.collection;

import com.zmops.iot.async.util.BiInt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 稀疏二维数组。
 * <p/>
 * 可以设置是否允许存入null。
 *
 * @author create by TcSnZh on 2021/5/14-下午9:45
 */
public class SparseArray2D<E> extends AbstractArray2D<E> {

    // ========== properties ==========

    /**
     * 限制长宽，默认为Integer.MAX_VALUE。稀疏数组不在乎这些。
     */
    private final int     maxLineLength;
    private final int     maxColumnLength;
    private final boolean allowNull;

    private final Map<BiInt, Object> items = new HashMap<>();

    // ========== index cache properties ==========

    /**
     * 缓存行列索引
     */
    private final NavigableMap<Integer, NavigableSet<Integer>> indexOfLine2columns = new TreeMap<>(Integer::compareTo);
    private final NavigableMap<Integer, NavigableSet<Integer>> indexOfColumn2lines = new TreeMap<>(Integer::compareTo);

    // ========== constructor ==========

    public SparseArray2D() {
        this(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public SparseArray2D(boolean allowNull) {
        this(Integer.MAX_VALUE, Integer.MAX_VALUE, allowNull);
    }

    public SparseArray2D(int maxLineCapacity, int maxColumnCapacity) {
        this(maxLineCapacity, maxColumnCapacity, false);
    }

    public SparseArray2D(int maxLineCapacity, int maxColumnCapacity, boolean allowNull) {
        this.maxLineLength = maxLineCapacity;
        this.maxColumnLength = maxColumnCapacity;
        this.allowNull = allowNull;
    }

    // ========== public methods ==========
    @Override
    public int lineLength() {
        return maxLineLength;
    }

    @Override
    public int columnLength() {
        return maxColumnLength;
    }

    @Override
    public E add(int line, int column, E element) {
        if (!allowNull && element == null) {
            throw new NullPointerException("null is not allowed");
        }
        Object put = items.put(BiInt.of(checkLine(line), checkColumn(column)), element == null ? NULL : element);
        addIndex(line, column);
        //noinspection unchecked
        return NULL.equals(put) ? null : (E) put;
    }

    @Override
    public E remove(int line, int column) {
        BiInt  idx = BiInt.of(checkLine(line), checkColumn(column));
        Object get = items.get(idx);
        if (get == null) {
            throw new IllegalArgumentException("There is no element in line " + line + " column " + column);
        }
        items.remove(idx);
        removeIndex(line, column);
        //noinspection unchecked
        return NULL.equals(get) ? null : (E) get;
    }

    /**
     * 该方法如果返回null，则分不清 之前存入了null 还是 没有存入过
     * <p>
     * {@inheritDoc}
     */
    @Override
    public E get(int line, int column) {
        Object get = items.get(BiInt.of(checkLine(line), checkColumn(column)));
        //noinspection unchecked
        return NULL.equals(get) ? null : (E) get;
    }

    @Override
    public boolean containsElement(E element) {
        if (NULL.equals(element)) {
            if (!allowNull) {
                return false;
            }
            return items.values().stream().anyMatch(v -> NULL.equals(element));
        }
        return items.values().stream().anyMatch(element::equals);
    }

    @Override
    public Map<Integer, E> fullLine(int line) {
        return Optional.ofNullable(indexOfLine2columns.get(line))
                .map(set -> set.stream()
                        .collect(Collectors.toMap(column -> column, column -> {
                            //noinspection unchecked
                            return (E) items.get(BiInt.of(line, column));
                        })))
                .orElse(Collections.emptyMap());
    }

    @Override
    public Map<Integer, E> fullColumn(int column) {
        return Optional.ofNullable(indexOfColumn2lines.get(column))
                .map(set -> set.stream()
                        .collect(Collectors.toMap(line -> line, line -> {
                            //noinspection unchecked
                            return (E) items.get(BiInt.of(line, column));
                        })))
                .orElse(Collections.emptyMap());
    }

    @Override
    public Iterator<? extends Point<E>> iterator(Comparator<BiInt> foreachOrder) {
        return new Iterator<Point<E>>() {
            private final Iterator<Map.Entry<BiInt, Object>> it;
            private Point<E> last = null;
            private boolean removed = false;

            {
                it = items.entrySet().stream()
                        .sorted((o1, o2) -> foreachOrder.compare(o1.getKey(), o2.getKey()))
                        .iterator();
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Point<E> next() {
                Map.Entry<BiInt, Object> next = it.next();
                removed = false;
                Object v = next.getValue();
                //noinspection unchecked
                return last = new PointImpl<>(next.getKey(), NULL.equals(v) ? null : (E) v);
            }

            @Override
            public void remove() {
                if (last == null || removed) {
                    throw new IllegalStateException(last == null
                            ? "Iterator has not yet been called .next() ."
                            : "Iterator item already removed : " + last);
                }
                BiInt idx = last.getIdx();
                SparseArray2D.this.remove(idx.getM(), idx.getN());
            }
        };
    }

    // ========== private methods ==========

    private int checkLine(int line) {
        int len = lineLength();
        if (line < 0 || line >= len) {
            throw new IndexOutOfBoundsException("Line " + line + " out of bound [0," + (len - 1) + "]");
        }
        return line;
    }

    private int checkColumn(int column) {
        int len = columnLength();
        if (column < 0 || column >= len) {
            throw new IndexOutOfBoundsException("Column " + column + " out of bound [0," + (len - 1) + "]");
        }
        return column;
    }

    private void addIndex(int line, int column) {
        indexOfLine2columns.computeIfAbsent(line, line1 -> new TreeSet<>(Integer::compareTo)).add(column);
        indexOfColumn2lines.computeIfAbsent(column, column1 -> new TreeSet<>(Integer::compareTo)).add(line);

    }

    private void removeIndex(int line, int column) {
        // remove line index
        {
            NavigableSet<Integer> columns = indexOfLine2columns.get(line);
            if (columns == null || !columns.contains(column)) {
                throw new ConcurrentModificationException(
                        "线程不安全导致索引异常 : lines " + columns + " is null or not contain line " + line);

            }
            if (columns.size() == 1) {
                indexOfLine2columns.remove(line);
            } else {
                columns.remove(column);
            }
        }
        // remove column index
        {
            NavigableSet<Integer> lines = indexOfColumn2lines.get(column);
            if (lines == null || !lines.contains(line)) {
                throw new ConcurrentModificationException(
                        "线程不安全导致索引异常 : lines " + lines + " is null or not contain column " + column);
            }
            if (lines.size() == 1) {
                indexOfColumn2lines.remove(column);
            } else {
                lines.remove(column);
            }
        }
    }
}
