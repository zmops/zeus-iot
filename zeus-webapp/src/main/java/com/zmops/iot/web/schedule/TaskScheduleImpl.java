package com.zmops.iot.web.schedule;

import com.zmops.iot.domain.schedule.Task;
import com.zmops.iot.schedule.config.ScheduleConfig;
import com.zmops.iot.schedule.cron.CronExpression;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author nantian created at 2021/11/12 10:54
 */

@Slf4j
public class TaskScheduleImpl {

    private final ScheduleConfig scheduleConfig;

    public TaskScheduleImpl(ScheduleConfig scheduleConfig) {
        this.scheduleConfig = scheduleConfig;
    }

    public static final long PRE_READ_MS = 5000; // pre read

    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private static final Map<Integer, List<Integer>> ringData = new ConcurrentHashMap<>();


    public void start() {
        scheduleThread = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis() % 1000);
            } catch (InterruptedException e) {
                if (!scheduleThreadToStop) {
                    log.error(e.getMessage(), e);
                }
            }

            int preReadCount = (scheduleConfig.getTriggerPoolFastMax() + scheduleConfig.getTriggerPoolSlowMax()) * 20;

            while (!scheduleThreadToStop) {
                long start = System.currentTimeMillis();
                boolean preReadSuc = true;

                try {
                    DB.beginTransaction();

                    long nowTime = System.currentTimeMillis();
                    List<Task> taskList = DB.findNative(Task.class,
                                    "SELECT T.ID, T.remark, T.trigger_status, T.trigger_next_time, T.schedule_type, T.schedule_conf " +
                                            "   FROM task_info AS T " +
                                            "   WHERE T.trigger_status = 1 " +
                                            "       and t.trigger_next_time <= :nextTime " +
                                            "   ORDER BY ID ASC " +
                                            "   LIMIT :preReadCount")
                            .setParameter("nextTime", nowTime + PRE_READ_MS)
                            .setParameter("preReadCount", preReadCount)
                            .findList();

                    if (taskList.size() > 0) {
                        for (Task task : taskList) {

                            if (nowTime > task.getTriggerNextTime() + PRE_READ_MS) {
                                MisfireStrategyEnum misfireStrategyEnum = MisfireStrategyEnum.match(task.getMisfireStrategy(), MisfireStrategyEnum.DO_NOTHING);

                                if (MisfireStrategyEnum.FIRE_ONCE_NOW == misfireStrategyEnum) {
                                    TaskTriggerPool.trigger(task.getId(), TriggerTypeEnum.MISFIRE, -1, null, null);
                                }
                                refreshNextValidTime(task, new Date());

                            } else if (nowTime > task.getTriggerNextTime()) {

                                TaskTriggerPool.trigger(task.getId(), TriggerTypeEnum.CRON, -1, null, null);
                                refreshNextValidTime(task, new Date());

                                if (task.getTriggerStatus() == 1 && nowTime + PRE_READ_MS > task.getTriggerNextTime()) {
                                    int ringSecond = (int) ((task.getTriggerNextTime() / 1000) % 60);
                                    pushTimeRing(ringSecond, task.getId());
                                    refreshNextValidTime(task, new Date(task.getTriggerNextTime()));
                                }
                            } else {
                                int ringSecond = (int) ((task.getTriggerNextTime() / 1000) % 60);
                                pushTimeRing(ringSecond, task.getId());
                                refreshNextValidTime(task, new Date(task.getTriggerNextTime()));
                            }
                        }
                        DB.updateAll(taskList);

                    } else {
                        preReadSuc = false;
                    }

                } catch (Exception e) {
                    if (!scheduleThreadToStop) {
                        log.error("JobScheduleHelper error: {}", e.getMessage());
                    }
                } finally {
                    DB.commitTransaction();
                }

                long cost = System.currentTimeMillis() - start;

                if (cost < 1000) {  // scan-overtime, not wait
                    try {
                        TimeUnit.MILLISECONDS.sleep((preReadSuc ? 1000 : PRE_READ_MS) - System.currentTimeMillis() % 1000);
                    } catch (InterruptedException e) {
                        if (!scheduleThreadToStop) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
            log.info("JobScheduleHelper#scheduleThread stop");
        });

        scheduleThread.setDaemon(true);
        scheduleThread.setName("JobScheduleHelper#scheduleThread");
        scheduleThread.start();

        ringThread = new Thread(() -> {
            while (!ringThreadToStop) {

                try {
                    TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                } catch (InterruptedException e) {
                    if (!ringThreadToStop) {
                        log.error(e.getMessage(), e);
                    }
                }

                try {
                    List<Integer> ringItemData = new ArrayList<>();
                    int nowSecond = Calendar.getInstance().get(Calendar.SECOND); // 避免处理耗时太长，跨过刻度，向前校验一个刻度；
                    for (int i = 0; i < 2; i++) {
                        List<Integer> tmpData = ringData.remove((nowSecond + 60 - i) % 60);
                        if (tmpData != null) {
                            ringItemData.addAll(tmpData);
                        }
                    }

                    log.debug("time-ring beat : " + nowSecond + " = " + Collections.singletonList(ringItemData));
                    if (ringItemData.size() > 0) {

                        for (int jobId : ringItemData) {
                            TaskTriggerPool.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null);
                        }
                        ringItemData.clear();
                    }
                } catch (Exception e) {
                    if (!ringThreadToStop) {
                        log.error("JobScheduleHelper#ringThread error:{}", e.getMessage());
                    }
                }
            }
            log.info("JobScheduleHelper#ringThread stop");
        });
        ringThread.setDaemon(true);
        ringThread.setName("admin JobScheduleHelper#ringThread");
        ringThread.start();
    }

    private void refreshNextValidTime(Task task, Date fromTime) throws Exception {
        Date nextValidTime = generateNextValidTime(task, fromTime);
        if (nextValidTime != null) {
            task.setTriggerLastTime(task.getTriggerNextTime());
            task.setTriggerNextTime(nextValidTime.getTime());
        } else {
            task.setTriggerStatus(0);
            task.setTriggerLastTime(0L);
            task.setTriggerNextTime(0L);
            log.warn("refreshNextValidTime fail for job: jobId={}, scheduleType={}, scheduleConf={}", task.getId(), task.getScheduleType(), task.getScheduleConf());
        }
    }

    private void pushTimeRing(int ringSecond, int jobId) {
        List<Integer> ringItemData = ringData.computeIfAbsent(ringSecond, k -> new ArrayList<Integer>());
        ringItemData.add(jobId);
    }


    /**
     * 停止任务调度
     */
    public void toStop() {

        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);  // wait
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        if (scheduleThread.getState() != Thread.State.TERMINATED) {
            scheduleThread.interrupt();
            try {
                scheduleThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        boolean hasRingData = false;
        if (!ringData.isEmpty()) {
            for (int second : ringData.keySet()) {
                List<Integer> tmpData = ringData.get(second);
                if (tmpData != null && tmpData.size() > 0) {
                    hasRingData = true;
                    break;
                }
            }
        }
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        ringThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        if (ringThread.getState() != Thread.State.TERMINATED) {
            ringThread.interrupt();
            try {
                ringThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.info("JobScheduleHelper stop");
    }

    /**
     * 根据表达式，获取下次任务调度执行时间
     *
     * @param task
     * @param fromTime
     * @return
     * @throws Exception
     */
    public static Date generateNextValidTime(Task task, Date fromTime) throws Exception {
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(task.getScheduleType(), null);
        if (ScheduleTypeEnum.CRON == scheduleTypeEnum) {
            return new CronExpression(task.getScheduleConf()).getNextValidTimeAfter(fromTime);
        }

        return null;
    }

}
