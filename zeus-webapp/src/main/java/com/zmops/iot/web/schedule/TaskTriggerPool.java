package com.zmops.iot.web.schedule;

import com.zmops.iot.web.schedule.config.ScheduleConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nantian created at 2021/11/12 22:10
 */

@Slf4j
public class TaskTriggerPool {

    private ThreadPoolExecutor fastTriggerPool = null;
    private ThreadPoolExecutor slowTriggerPool = null;


    private final ScheduleConfig scheduleConfig;

    public TaskTriggerPool(ScheduleConfig scheduleConfig) {
        this.scheduleConfig = scheduleConfig;
        helper = this;
    }

    public void start() {
        fastTriggerPool = new ThreadPoolExecutor(
                10,
                scheduleConfig.getTriggerPoolFastMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> new Thread(r, "JobTriggerPoolHelper-fastTriggerPool-" + r.hashCode()));

        slowTriggerPool = new ThreadPoolExecutor(
                10,
                scheduleConfig.getTriggerPoolSlowMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                r -> new Thread(r, "JobTriggerPoolHelper-slowTriggerPool-" + r.hashCode()));
    }


    public void stop() {
        fastTriggerPool.shutdownNow();
        slowTriggerPool.shutdownNow();
    }

    private volatile long minTim = System.currentTimeMillis() / 60000;
    private final ConcurrentMap<Integer, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();


    public void addTrigger(final int jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorParam,
                           final String addressList) {

        ThreadPoolExecutor triggerPool_ = fastTriggerPool;
        AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);
        if (jobTimeoutCount != null && jobTimeoutCount.get() > 10) {  // job-timeout 10 times in 1 min
            triggerPool_ = slowTriggerPool;
        }

        triggerPool_.execute(() -> {
            long start = System.currentTimeMillis();

            try {
                log.info("jobid : {}", jobId);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {

                long minTim_now = System.currentTimeMillis() / 60000;
                if (minTim != minTim_now) {
                    minTim = minTim_now;
                    jobTimeoutCountMap.clear();
                }

                long cost = System.currentTimeMillis() - start;
                if (cost > 500) {
                    AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                    if (timeoutCount != null) {
                        timeoutCount.incrementAndGet();
                    }
                }
            }
        });
    }

    private static TaskTriggerPool helper;

    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorParam, String addressList) {
        helper.addTrigger(jobId, triggerType, failRetryCount, executorParam, addressList);
    }
}
