package com.zmops.iot.async.callback;


import com.zmops.iot.async.worker.WorkResult;

/**
 * 默认回调类，如果不设置的话，会默认给这个回调
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
public class DefaultCallback<T, V> implements ICallback<T, V> {
    private static final DefaultCallback<Object, Object> instance = new DefaultCallback<Object, Object>() {
        @Override
        public String toString() {
            return "(DefaultCallback instance)";
        }
    };

    public static DefaultCallback<Object, Object> getInstance() {
        return instance;
    }

    @Override
    public void begin() {
        // do nothing
    }

    /**
     * 默认情况啥回调都没有，而且将吞掉所有异常显示（只保存在{@link WorkResult}中）
     */
    @Override
    public void result(boolean success, T param, WorkResult<V> workResult) {
        // do nothing
    }
}
