package com.zmops.zeus.iot.server.transfer.core.task;


import com.zmops.zeus.iot.server.transfer.conf.TransferConfiguration;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.common.AbstractDaemon;
import com.zmops.zeus.iot.server.transfer.common.TransferThreadFactory;
import com.zmops.zeus.iot.server.transfer.core.job.JobManager;
import com.zmops.zeus.iot.server.transfer.core.TransferManager;
import com.zmops.zeus.iot.server.transfer.utils.TransferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Task manager maintains lots of tasks and communicate with job level components.
 * It also provide functions to execute commands from job level like killing/submit tasks.
 */
public class TaskManager extends AbstractDaemon {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    // task thread pool;
    private final ThreadPoolExecutor runningPool;
    private final TransferManager    transferManager;
    private final TaskMetrics        taskMetrics;

    private final ConcurrentHashMap<String, TaskWrapper> tasks;
    private final BlockingQueue<TaskWrapper>             retryTasks;

    private final int  monitorInterval;
    private final int  taskMaxCapacity;
    private final int  taskRetryMaxTime;
    private final long waitTime;

    /**
     * Init task manager.
     *
     * @param transferManager - agent manager
     */
    public TaskManager(TransferManager transferManager) {
        this.transferManager = transferManager;
        this.runningPool = new ThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new TransferThreadFactory("task"));

        // metric for task level
        taskMetrics = TaskMetrics.create();
        tasks = new ConcurrentHashMap<>();

        TransferConfiguration conf = TransferConfiguration.getAgentConf();

        retryTasks = new LinkedBlockingQueue<>(conf.getInt(TransferConstants.TASK_RETRY_MAX_CAPACITY, TransferConstants.DEFAULT_TASK_RETRY_MAX_CAPACITY));

        monitorInterval = conf.getInt(TransferConstants.TASK_MONITOR_INTERVAL, TransferConstants.DEFAULT_TASK_MONITOR_INTERVAL);
        taskRetryMaxTime = conf.getInt(TransferConstants.TASK_RETRY_SUBMIT_WAIT_SECONDS, TransferConstants.DEFAULT_TASK_RETRY_SUBMIT_WAIT_SECONDS);
        taskMaxCapacity = conf.getInt(TransferConstants.TASK_RETRY_MAX_CAPACITY, TransferConstants.DEFAULT_TASK_RETRY_MAX_CAPACITY);
        waitTime = conf.getLong(TransferConstants.THREAD_POOL_AWAIT_TIME, TransferConstants.DEFAULT_THREAD_POOL_AWAIT_TIME);
    }

    /**
     * Get task metrics
     *
     * @return task metrics
     */
    public TaskMetrics getTaskMetrics() {
        return taskMetrics;
    }

    public TaskWrapper getTaskWrapper(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * submit task, wait if task queue is full.
     *
     * @param task - task
     */
    public void submitTask(Task task) {
        TaskWrapper taskWrapper = new TaskWrapper(transferManager, task);
        submitTask(taskWrapper);

    }

    public void submitTask(TaskWrapper wrapper) {
        TaskWrapper retTaskWrapper = tasks.putIfAbsent(wrapper.getTask().getTaskId(), wrapper);

        if (retTaskWrapper == null) {
            // pool may be full
            boolean notSubmitted = true;
            while (notSubmitted) {
                try {
                    this.runningPool.submit(wrapper);
                    notSubmitted = false;
                } catch (Exception ex) {
                    TransferUtils.silenceSleepInMs(waitTime);
                    LOGGER.warn("reject task {}", wrapper.getTask().getTaskId(), ex);
                }
            }

            taskMetrics.runningTasks.incr();
        }
    }

    /**
     * retry task.
     *
     * @param wrapper - task wrapper
     */
    private boolean addRetryTask(TaskWrapper wrapper) {
        LOGGER.info("retry submit task {}", wrapper.getTask().getTaskId());
        try {
            boolean success = retryTasks.offer(wrapper, taskRetryMaxTime, TimeUnit.SECONDS);
            if (!success) {
                LOGGER.error("cannot submit to retry queue, max {}, current {}", taskMaxCapacity, retryTasks.size());
            } else {
                taskMetrics.retryingTasks.incr();
            }
            return success;
        } catch (Exception ex) {
            LOGGER.error("error while offer task", ex);
        }
        return false;
    }

    /**
     * Check whether task is finished
     *
     * @param taskId - task id
     * @return - true if task is finished otherwise false
     */
    public boolean isTaskFinished(String taskId) {
        TaskWrapper wrapper = tasks.get(taskId);
        if (wrapper != null) {
            return wrapper.isFinished();
        }
        return false;
    }

    /**
     * Check if task is success
     *
     * @param taskId task id
     * @return true if task is success otherwise false
     */
    public boolean isTaskSuccess(String taskId) {
        TaskWrapper wrapper = tasks.get(taskId);
        if (wrapper != null) {
            return wrapper.isSuccess();
        }
        return false;
    }

    /**
     * Remove task and wait task to finish by task id
     *
     * @param taskId - task id
     */
    public void removeTask(String taskId) {
        taskMetrics.runningTasks.decr();
        TaskWrapper taskWrapper = tasks.remove(taskId);
        if (taskWrapper != null) {
            taskWrapper.waitForFinish();
        }
    }

    /**
     * kill task
     *
     * @param task task
     * @return
     */
    public boolean killTask(Task task) {
        // kill running tasks.
        TaskWrapper taskWrapper = tasks.get(task.getTaskId());
        if (taskWrapper != null) {
            taskWrapper.kill();
            return true;
        }
        return false;
    }

    /**
     * Thread for checking whether task should retry.
     *
     * @return - runnable thread
     */
    public Runnable createTaskMonitorThread() {
        return () -> {
            while (isRunnable()) {
                try {
                    for (String taskId : tasks.keySet()) {
                        TaskWrapper wrapper = tasks.get(taskId);
                        if (wrapper != null && wrapper.isFailed() && wrapper.shouldRetry()) {
                            boolean success = addRetryTask(wrapper);
                            if (success) {
                                removeTask(taskId);
                            }
                        }
                    }
                    while (!retryTasks.isEmpty()) {
                        TaskWrapper taskWrapper = retryTasks.poll();
                        if (taskWrapper != null) {
                            taskMetrics.retryingTasks.decr();
                            submitTask(taskWrapper);
                        }
                    }
                    TimeUnit.SECONDS.sleep(monitorInterval);
                } catch (Exception ex) {
                    LOGGER.error("Exception caught", ex);
                }
            }
        };
    }

    /**
     * start service.
     */
    @Override
    public void start() {
        submitWorker(createTaskMonitorThread());
    }

    /**
     * stop service.
     */
    @Override
    public void stop() throws Exception {
        waitForTerminate();
        this.runningPool.shutdown();
    }
}
