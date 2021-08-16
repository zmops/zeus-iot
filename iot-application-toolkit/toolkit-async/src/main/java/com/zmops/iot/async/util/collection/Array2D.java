package com.zmops.iot.async.util.collection;

import com.zmops.iot.async.util.BiInt;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 二维数组
 *
 * @author create by TcSnZh on 2021/5/14-下午9:50
 */
@SuppressWarnings("unused")
public interface Array2D<E> extends Iterable<Array2D.Point<E>> {
    /**
     * 有多少行
     */
    int lineLength();

    /**
     * 有多少列
     */
    int columnLength();

    /**
     * 添加元素到指定位置
     *
     * @param line    行
     * @param column  列
     * @param element 元素
     * @return 如果之前添加过元素，将返回替换掉的之前的元素
     * @throws IndexOutOfBoundsException 行列超出范围
     */
    E add(int line, int column, E element);

    /**
     * 如果不存在的话则添加元素
     * <p>
     * {@link #add(int, int, Object)}
     *
     * @return 不存在且成功添加，返回true。
     */
    default boolean addIfAbsent(int line, int column, E element) {
        if (get(line, column) != null) {
            return false;
        }
        add(line, column, element);
        return true;
    }

    /**
     * 删除元素
     *
     * @param line   行
     * @param column 列
     * @return 返回移出的元素
     * @throws IndexOutOfBoundsException 行列超出返回
     * @throws IllegalArgumentException  如果原本不存在元素
     */
    E remove(int line, int column);

    /**
     * 存在则移除，不存在则返回null
     *
     * @param line   行
     * @param column 列
     * @return 如果不存在，返回null。存在则返回被移出的元素。
     * @throws IndexOutOfBoundsException 行列超出范围
     */
    default E removeIfAbsent(int line, int column) {
        if (get(line, column) == null) {
            return null;
        }
        return remove(line, column);
    }

    /**
     * 获取元素
     *
     * @param line   行
     * @param column 列
     * @return 如果存在，返回该元素。不存在则返回null。
     * @throws IndexOutOfBoundsException 行列超出范围
     */
    E get(int line, int column);

    /**
     * 是否包含元素
     *
     * @param element 元素
     * @return 有这个元素就返回true。
     */
    boolean containsElement(E element);

    /**
     * 获取整行的元素
     *
     * @param line 行号
     * @return 返回key为列号，value为元素的Map
     * @throws IndexOutOfBoundsException 行号超出范围
     */
    Map<Integer, E> fullLine(int line);

    /**
     * 获取整列的元素
     *
     * @param column 列号
     * @return 返回key为行号，value为元素的Map
     * @throws IndexOutOfBoundsException 列号超出范围
     */
    Map<Integer, E> fullColumn(int column);

    /**
     * 迭代器
     *
     * @param foreachOrder 遍历顺序
     * @return 如果本容器不允许null值存在，只需返回存在的元素的键即可。如果允许null值存在，仅需返回包括人工放入的null值的键即可。
     */
    Iterator<? extends Point<E>> iterator(Comparator<BiInt> foreachOrder);

    @Override
    default Iterator<Point<E>> iterator() {
        //noinspection unchecked
        return (Iterator) iterator(BiInt.cmp_m_asc_n_asc);
    }

    /**
     * 流
     */
    default Stream<? extends Point<E>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<? extends Point<E>> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    default Spliterator<Point<E>> spliterator(Comparator<BiInt> foreachOrder) {
        return Spliterators.spliteratorUnknownSize(iterator(foreachOrder), 0);
    }

    default Stream<? extends Point<E>> stream(Comparator<BiInt> foreachOrder) {
        return StreamSupport.stream(spliterator(foreachOrder), false);
    }

    default Stream<? extends Point<E>> parallelStream(Comparator<BiInt> foreachOrder) {
        return StreamSupport.stream(spliterator(foreachOrder), true);
    }

    /**
     * 端点
     *
     * @param <E> 元素泛型
     */
    interface Point<E> {
        BiInt getIdx();

        default int getLine() {
            return getIdx().getM();
        }

        default int getColumn() {
            return getIdx().getN();
        }

        E getElement();
    }
}
