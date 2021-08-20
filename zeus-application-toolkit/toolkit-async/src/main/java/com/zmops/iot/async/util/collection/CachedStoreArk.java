package com.zmops.iot.async.util.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 一个缓存元素位置的储存柜。
 *
 * @author create by TcSnZh on 2021/5/14-上午2:37
 */
public class CachedStoreArk<E> extends AbstractStoreArk<E> {
    private final StoreArk<E> inner;

    private final Map<E, Integer> cacheMap = new HashMap<>();

    public CachedStoreArk() {
        this(CommonStoreArk::new);
    }

    private CachedStoreArk(Supplier<StoreArk<E>> sup) {
        this.inner = sup.get();
    }

    @Override
    public int store(E element) {
        int id = inner.store(element);
        cacheMap.put(element, id);
        return id;
    }

    @Override
    public E peek(int id) {
        return inner.peek(id);
    }

    @Override
    public E takeOut(int id) {
        E e = inner.takeOut(id);
        cacheMap.remove(e);
        return e;
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public Iterator<Map.Entry<Integer, E>> iterator() {
        return inner.iterator();
    }

    @Override
    public int findId(E element) {
        Integer idNullable = cacheMap.get(element);
        return idNullable == null ? -1 : idNullable;
    }
}
