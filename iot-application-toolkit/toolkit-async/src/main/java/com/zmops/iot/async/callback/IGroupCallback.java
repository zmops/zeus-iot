package com.zmops.iot.async.callback;

import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.worker.OnceWork;
import com.zmops.iot.async.wrapper.WorkerWrapper;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 如果是异步执行整组的话，可以用这个组回调。<b>已经废弃</b>
 *
 * @author wuweifeng wrote on 2019-11-19.
 * @deprecated deprecate at version 1.5.1
 * <p>
 * please use {@link Async#work(long, ExecutorService, Collection, String)}.
 * <p>
 * 该方法返回的{@link OnceWork}句柄，默认不会同步等待结束，
 * 这便替代了原先的
 * {@link Async#beginWorkAsync(long, ExecutorService, IGroupCallback, WorkerWrapper[])}
 * <p>
 * 需要同步等待的话调用{@link OnceWork#awaitFinish()}即可。
 * </p>
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface IGroupCallback {
    /**
     * 成功后，可以从wrapper里去getWorkResult
     */
    void success(List<WorkerWrapper> workerWrappers);

    /**
     * 失败了，也可以从wrapper里去getWorkResult
     */
    void failure(List<WorkerWrapper> workerWrappers, Exception e);
}
