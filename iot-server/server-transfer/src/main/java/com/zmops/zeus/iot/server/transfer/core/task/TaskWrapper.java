package com.zmops.zeus.iot.server.transfer.core.task;


import com.zmops.zeus.iot.server.transfer.conf.TransferConfiguration;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.core.api.Message;
import com.zmops.zeus.iot.server.transfer.core.common.AgentThreadFactory;
import com.zmops.zeus.iot.server.transfer.core.manager.TransferManager;
import com.zmops.zeus.iot.server.transfer.core.message.EndMessage;
import com.zmops.zeus.iot.server.transfer.core.state.AbstractStateWrapper;
import com.zmops.zeus.iot.server.transfer.core.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskWrapper is used in taskManager, it maintains the life cycle of
 * running task.
 */
public class TaskWrapper extends AbstractStateWrapper {

    private static final Logger LOGGER               = LoggerFactory.getLogger(TaskWrapper.class);
    public static final  int    WAIT_FINISH_TIME_OUT = 1;

    private final TaskManager taskManager;
    private final Task        task;

    private final AtomicInteger retryTime = new AtomicInteger(0);

    private final int maxRetryTime;
    private final int pushMaxWaitTime;
    private final int pullMaxWaitTime;

    private ExecutorService executorService;

    public TaskWrapper(TransferManager manager, Task task) {
        super();
        this.taskManager = manager.getTaskManager();
        this.task = task;

        TransferConfiguration conf = TransferConfiguration.getAgentConf();

        maxRetryTime = conf.getInt(TransferConstants.TASK_MAX_RETRY_TIME, TransferConstants.DEFAULT_TASK_MAX_RETRY_TIME);
        pushMaxWaitTime = conf.getInt(TransferConstants.TASK_PUSH_MAX_SECOND, TransferConstants.DEFAULT_TASK_PUSH_MAX_SECOND);
        pullMaxWaitTime = conf.getInt(TransferConstants.TASK_PULL_MAX_SECOND, TransferConstants.DEFAULT_TASK_PULL_MAX_SECOND);

        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    new AgentThreadFactory("task-reader-writer"));
        }

        doChangeState(State.ACCEPTED);
    }

    /**
     * submit read thread
     *
     * @return CompletableFuture
     */
    private CompletableFuture<?> submitReadThread() {
        return CompletableFuture.runAsync(() -> {
            Message message = null;
            while (!isException() && !task.isReadFinished()) {
                if (message == null || task.getChannel().push(message, pushMaxWaitTime, TimeUnit.SECONDS)) {
                    message = task.getReader().read();
                }
            }
            LOGGER.info("read end, task exception status is {}, read finish status is {}", isException(), task.isReadFinished());
            // write end message
            task.getChannel().push(new EndMessage());
        }, executorService);
    }

    /**
     * submit write thread
     *
     * @return CompletableFuture
     */
    private CompletableFuture<?> submitWriteThread() {
        return CompletableFuture.runAsync(() -> {
            while (!isException()) {
                Message message = task.getChannel().pull(pullMaxWaitTime, TimeUnit.SECONDS);
                if (message instanceof EndMessage) {
                    break;
                }
                task.getSink().write(message);
            }
        }, executorService);
    }

    /**
     * submit reader/writer
     */
    private void submitThreadsAndWait() {
        CompletableFuture<?> reader = submitReadThread();
        CompletableFuture<?> writer = submitWriteThread();

        CompletableFuture.allOf(reader, writer).exceptionally(ex -> {
            doChangeState(State.FAILED);
            LOGGER.error("exception caught", ex);
            return null;
        }).join();
    }

    /**
     * kill task
     */
    void kill() {
        LOGGER.info("task id {} is killed", task.getTaskId());
        doChangeState(State.KILLED);
    }

    /**
     * In standalone mode, the job to be removed should wait until the read is finished, set
     * timeout to WAIT_FINISH_TIME_OUT minute to wait for finishing
     */
    void waitForFinish() {
        LOGGER.info("set readTime out to 1 minute task id is {}", task.getTaskId());
        task.getReader().setReadTimeout(TimeUnit.MINUTES.toMillis(WAIT_FINISH_TIME_OUT));
    }

    /**
     * whether task retry times exceed max retry time.
     *
     * @return - whether should retry
     */
    boolean shouldRetry() {
        return retryTime.get() < maxRetryTime;
    }

    Task getTask() {
        return task;
    }

    @Override
    public void addCallbacks() {
        this.addCallback(State.ACCEPTED, State.RUNNING, (before, after) -> {

        }).addCallback(State.RUNNING, State.FAILED, (before, after) -> {
            LOGGER.info("task {} is failed, please check it", task.getTaskId());
            retryTime.incrementAndGet();
            if (!shouldRetry()) {
                doChangeState(State.FATAL);
                taskManager.getTaskMetrics().fatalTasks.incr();
            }
        }).addCallback(State.FAILED, State.FATAL, (before, after) -> {

        }).addCallback(State.FAILED, State.ACCEPTED, (before, after) -> {

        }).addCallback(State.FAILED, State.RUNNING, ((before, after) -> {

        })).addCallback(State.RUNNING, State.SUCCEEDED, (before, after) -> {

        });
    }


    @Override
    public void run() {
        try {
            LOGGER.info("start to run {}, retry time is {}", task.getTaskId(), retryTime.get());
            doChangeState(State.RUNNING);
            task.init();

            submitThreadsAndWait();
            if (!isException()) {
                doChangeState(State.SUCCEEDED);
            }

            LOGGER.info("start to destroy task {}", task.getTaskId());
            task.destroy();
        } catch (Exception ex) {
            LOGGER.error("error while running wrapper", ex);
            doChangeState(State.FAILED);
        }
    }
}
