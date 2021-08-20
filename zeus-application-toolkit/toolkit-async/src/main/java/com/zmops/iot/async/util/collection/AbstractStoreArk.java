package com.zmops.iot.async.util.collection;

/**
 * @author create by TcSnZh on 2021/5/14-上午2:33
 */
public abstract class AbstractStoreArk<E> implements StoreArk<E> {

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(size() * 10).append(this.getClass().getSimpleName()).append("{");
        if (!isEmpty()) {
            stream().forEach(entry -> sb.append(entry.getKey()).append(":").append(entry.getValue()).append(", "));
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.append("}").toString();
    }
}
