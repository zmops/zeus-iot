package com.zmops.iot.async.executor;

import com.zmops.iot.async.util.timer.HashedWheelTimer;
import com.zmops.iot.async.util.timer.Timeout;
import com.zmops.iot.async.util.timer.Timer;
import com.zmops.iot.async.util.timer.TimerTask;
import com.zmops.iot.async.wrapper.WorkerWrapperGroup;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 检查{@link WorkerWrapperGroup}是否调用完成的轮询中心。
 * 内部使用时间轮进行轮询。
 * <p>
 * ===========================================================================================
 * <p>
 * 在v1.4及以前的版本，存在如下问题：
 * >
 * 在使用线程数量较少的线程池进行beginWork时，调用WorkerWrapper#beginNext方法时，
 * 会因为本线程等待下游Wrapper执行完成而存在线程耗尽bug。线程池会死翘翘的僵住、动弹不得。
 * >
 * 例如仅有2个线程的线程池，执行以下任务：
 * {@code
 * <p>
 * 这是旧版本(v1.4及以前)中可能会引发线程耗尽bug的情况，在test/v15.wrappertest中示例testThreadPolling_V14Bug说明了这个bug
 * 线程数：2
 * A(5ms)--B1(10ms) ---|--> C1(5ms)
 * .  \                |             (B1、B2任一完成可执行C1、C2)
 * .   ---> B2(20ms) --|--> C2(5ms)
 * <p>
 * }
 * 线程1执行了A，然后在{@link CompletableFuture#allOf(CompletableFuture[])}等待B1与B2执行完成。
 * 线程2执行了B1或B2中的一个，也在allOf方法等待C1、C2完成。
 * 结果没有线程执行C和B2了，导致超时而死，并且这个线程池线程有可能被耗尽。
 * >
 *
 * @author create by TcSnZh on 2021/5/9-下午9:22
 */
public class PollingCenter {

    // ========== singleton instance ==========

    private static final PollingCenter instance = new PollingCenter();

    public static PollingCenter getInstance() {
        return instance;
    }

    // ========== fields and methods ==========

    public void checkGroup(WorkerWrapperGroup.CheckFinishTask task) {
        checkGroup(task, 0);
    }

    public void checkGroup(WorkerWrapperGroup.CheckFinishTask task, long daley) {
        timer.newTimeout(task, daley, TimeUnit.MILLISECONDS);
    }

    private final Timer timer = new Timer() {
        private final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(
                r -> {
                    Thread thread = new Thread(r, "asyncTool-pollingThread");
                    thread.setDaemon(true);
                    return thread;
                },
                1,
                TimeUnit.MILLISECONDS,
                1024);

        @Override
        public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
            return hashedWheelTimer.newTimeout(task, delay, unit);
        }

        @Override
        public Set<? extends Timeout> stop() {
            return hashedWheelTimer.stop();
        }

        @Override
        public String toString() {
            return "PollingCenter.timer";
        }
    };
}
