package com.zmops.zeus.iot.server.transfer.core.job;


import com.zmops.zeus.iot.server.transfer.conf.TransferConfiguration;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.core.manager.TransferManager;
import com.zmops.zeus.iot.server.transfer.core.state.AbstractStateWrapper;
import com.zmops.zeus.iot.server.transfer.core.state.State;
import com.zmops.zeus.iot.server.transfer.core.task.Task;
import com.zmops.zeus.iot.server.transfer.core.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JobWrapper is used in JobManager, it defines the life cycle of
 * running job and maintains the state of job.
 */
public class JobWrapper extends AbstractStateWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobWrapper.class);

    private final TransferConfiguration transferConfig;
    private final TaskManager           taskManager;
    private final JobManager            jobManager;
    private final Job                   job;

    private final List<Task> allTasks;

    public JobWrapper(TransferManager manager, Job job) {
        super();
        this.transferConfig = TransferConfiguration.getAgentConf();
        this.taskManager = manager.getTaskManager();
        this.jobManager = manager.getJobManager();
        this.job = job;
        this.allTasks = new ArrayList<>();
        doChangeState(State.ACCEPTED);
    }

    /**
     * check states of all tasks, wait if one of them not finished.
     */
    private void checkAllTasksStateAndWait() throws Exception {
        boolean isFinished = false;

        long checkInterval = transferConfig.getLong(TransferConstants.JOB_FINISH_CHECK_INTERVAL, TransferConstants.DEFAULT_JOB_FINISH_CHECK_INTERVAL);
        do {
            // check whether all tasks have finished.
            isFinished = allTasks.stream().allMatch(task -> taskManager.isTaskFinished(task.getTaskId()));
            TimeUnit.SECONDS.sleep(checkInterval);
        } while (!isFinished);

        LOGGER.info("all tasks of {} has been checked", job.getJobInstanceId());
        boolean isSuccess = allTasks.stream().allMatch(task -> taskManager.isTaskSuccess(task.getTaskId()));

        if (isSuccess) {
            doChangeState(State.SUCCEEDED);
        } else {
            doChangeState(State.FAILED);
        }
    }

    /**
     * submit all tasks
     */
    private void submitAllTasks() {
        List<Task> tasks = job.createTasks();

        tasks.forEach(task -> {
            allTasks.add(task);
            taskManager.submitTask(task);
        });
    }

    /**
     * get job
     *
     * @return job
     */
    public Job getJob() {
        return job;
    }

    /**
     * cleanup job
     */
    public void cleanup() {
        allTasks.forEach(task -> taskManager.removeTask(task.getTaskId()));
    }

    @Override
    public void run() {
        try {
            doChangeState(State.RUNNING);

            submitAllTasks();

            checkAllTasksStateAndWait();

            cleanup();
        } catch (Exception ex) {
            doChangeState(State.FAILED);
            LOGGER.error("error caught: {}, message: {}", job.getJobConf().toJsonStr(), ex.getMessage());
        }
    }

    @Override
    public void addCallbacks() {
        this.addCallback(State.ACCEPTED, State.RUNNING, (before, after) -> {

        }).addCallback(State.RUNNING, State.FAILED, (before, after) -> {
            jobManager.markJobAsFailed(job.getJobInstanceId());
        }).addCallback(State.RUNNING, State.SUCCEEDED, ((before, after) -> {
            jobManager.markJobAsSuccess(job.getJobInstanceId());
        }));
    }
}
