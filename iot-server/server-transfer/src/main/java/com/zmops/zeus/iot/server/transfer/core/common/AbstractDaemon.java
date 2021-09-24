package com.zmops.zeus.iot.server.transfer.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Providing work threads management, those threads run
 * periodically until agent is stopped.
 */
public abstract class AbstractDaemon implements Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDaemon.class);

    /**
     * worker thread pool, share it
     **/
    private static final ExecutorService WORKER_SERVICES = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new AgentThreadFactory("AbstractDaemon")
    );

    private final List<CompletableFuture<?>> workerFutures;

    private boolean runnable = true;

    public AbstractDaemon() {
        this.workerFutures = new ArrayList<>();
    }

    /**
     * Whether threads can in running state with while loop.
     *
     * @return - true if threads can run
     */
    public boolean isRunnable() {
        return runnable;
    }

    /**
     * Stop running threads.
     */
    public void stopRunningThreads() {
        runnable = false;
    }

    /**
     * Submit work thread to thread pool.
     *
     * @param worker - work thread
     */
    public void submitWorker(Runnable worker) {
        CompletableFuture<?> future = CompletableFuture.runAsync(worker, WORKER_SERVICES);
        workerFutures.add(future);
        LOGGER.info("{} running worker number is {}", this.getClass().getName(), workerFutures.size());
    }

    /**
     * Wait for threads finish.
     */
    @Override
    public void join() {
        for (CompletableFuture<?> future : workerFutures) {
            future.join();
        }
    }

    /**
     * Stop thread pool and running threads if they're in the running state.
     */
    public void waitForTerminate() {
        // stop running threads.
        if (isRunnable()) {
            stopRunningThreads();
        }
    }
}
