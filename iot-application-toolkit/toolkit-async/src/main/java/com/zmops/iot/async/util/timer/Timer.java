package com.zmops.iot.async.util.timer;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 照抄netty
 * 让{@link TimerTask}在后台线程中执行。
 *
 * @author create by TcSnZh on 2021/5/9-下午6:33
 */
public interface Timer {

    /**
     * 使{@link TimerTask}在指定的延迟后执行一次。
     *
     * @param delay 延时长度
     * @param unit  延时单位
     * @return 返回 {@link Timeout}关系类
     * @throws IllegalStateException      如果此计时器已经已停止
     * @throws RejectedExecutionException 如果挂起的超时太多，则创建新的超时会导致系统不稳定。
     */
    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);

    @SuppressWarnings("unused")
    default Timeout newTimeout(Runnable runnable, long delay, TimeUnit unit) {
        AtomicReference<Timeout> timeoutRef = new AtomicReference<>();
        newTimeout(timeout -> {
            timeoutRef.set(timeout);
            runnable.run();
        }, delay, unit);
        return timeoutRef.get();
    }

    /**
     * 释放此{@link Timer}所有资源（例如线程），并取消所有尚未执行的任务。
     *
     * @return 与被该方法取消的任务相关联的 {@link Timeout}
     */
    Set<? extends Timeout> stop();
}
