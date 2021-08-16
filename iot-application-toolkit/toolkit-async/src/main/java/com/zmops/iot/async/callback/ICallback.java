package com.zmops.iot.async.callback;


import com.zmops.iot.async.exception.EndsNormallyException;
import com.zmops.iot.async.worker.WorkResult;

/**
 * 每个执行单元执行完毕后，会回调该接口</p>
 * 需要监听执行结果的，实现该接口即可
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
@FunctionalInterface
public interface ICallback<T, V> {
    /**
     * 任务开始的监听
     */
    default void begin() {

    }

    /**
     * 耗时操作执行完毕后，就给value注入值
     * <p/>
     * 只要Wrapper被调用后成功或失败/超时，该方法都会被执行。
     */
    void result(boolean success, T param, WorkResult<V> workResult);

    /**
     * 提供常量选项：
     * <p/>
     * 如果发生了异常，则打印异常信息。
     * 正常结束（例如取消、跳过）的异常{@link com.zmops.iot.async.exception.EndsNormallyException}不会打印。
     */
    ICallback PRINT_EXCEPTION_STACK_TRACE = new ICallback<Object, Object>() {
        @Override
        public void result(boolean success, Object param, WorkResult<Object> workResult) {
            Exception ex = workResult.getEx();
            if (ex != null && !(ex instanceof EndsNormallyException)) {
                ex.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "PRINT_EXCEPTION_STACK_TRACE";
        }
    };
}
