package com.zmops.iot.async.util.timer;

/**
 * 借鉴netty。
 * 一个连接着{@link Timer}和{@link TimerTask}，表示着任务状态的“关系类”
 *
 * @author create by TcSnZh on 2021/5/9-下午6:33
 */
public interface Timeout {
    /**
     * 返回对应的{@link Timer}。
     */
    Timer timer();

    /**
     * 返回对应的{@link TimerTask}
     */
    TimerTask task();

    /**
     * 当且仅当关联的{@link TimerTask}已超时时，才返回{@code true}。
     */
    boolean isExpired();

    /**
     * 当且仅当关联的{@link TimerTask}被取消时，才返回{@code true}。
     */
    boolean isCancelled();

    /**
     * 尝试取消关联的{@link TimerTask}。如果任务已经执行或已取消，它将无副作用地返回。
     *
     * @return 如果取消成功完成，则为true，否则为false
     */
    @SuppressWarnings("unused")
    boolean cancel();
}
