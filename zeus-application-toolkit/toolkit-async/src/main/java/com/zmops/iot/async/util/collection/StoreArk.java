package com.zmops.iot.async.util.collection;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * id存储柜。
 * 每个元素的id是固定的（除非取出后重新加入），且id是大于等于0，且分配到的id必须是未分配的id中最小的。
 * <p>
 * 类似于我们去游泳馆，里面的存放个人物品的柜子。
 * 放进去元素后，会分配一个id。然后凭借该id取出元素。
 * 不过不同于这些现实中的柜子的是，这个存储柜必定会提供最小的id，并且必定>0。
 * </p>
 *
 * @author create by TcSnZh on 2021/5/14-上午2:29
 */
public interface StoreArk<E> extends Iterable<Map.Entry<Integer, E>> {
    /**
     * 存入元素
     *
     * @param element 元素。
     * @return 返回最小的id。从0开始。
     */
    int store(E element);

    /**
     * 查看元素
     *
     * @param id id;
     * @return 返回存在的元素。如果本id未被占用 或 原先存入null，返回null。
     * @throws IllegalArgumentException id为负数时抛出该异常
     */
    E peek(int id);

    /**
     * 取出元素
     *
     * @param id id
     * @return 返回被取出的元素。如果本id未被占用 或 原先存入null，返回null。
     * @throws IllegalArgumentException id为负数时抛出该异常
     */
    E takeOut(int id);

    /**
     * 元素个数
     */
    int size();

    /**
     * 是否为空
     */
    boolean isEmpty();

    /**
     * 查找元素的id
     *
     * @param element 元素
     * @return 如果存在，返回id。不存在返回-1
     */
    int findId(E element);

    /**
     * 返回流
     */
    default Stream<Map.Entry<Integer, E>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
