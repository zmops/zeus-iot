package com.zmops.zeus.iot.server.transfer.core.job;


import com.zmops.zeus.iot.server.transfer.common.AbstractDaemon;
import com.zmops.zeus.iot.server.transfer.common.TransferThreadFactory;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.conf.TransferConfiguration;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.core.TransferManager;
import com.zmops.zeus.iot.server.transfer.core.db.JobProfileDb;
import com.zmops.zeus.iot.server.transfer.core.db.StateSearchKey;
import com.zmops.zeus.iot.server.transfer.utils.TransferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.*;
import static com.zmops.zeus.iot.server.transfer.conf.TransferConstants.*;

/**
 * JobManager maintains lots of jobs, and communicate between server and task manager.
 */
public class JobManager extends AbstractDaemon {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    // key is job instance id.
    private       ConcurrentHashMap<String, JobWrapper> jobs;
    // jobs which are not accepted by running pool.
    private final ConcurrentHashMap<String, Job>        pendingJobs;
    // job thread pool
    private final ThreadPoolExecutor                    runningPool;
    private final TransferManager                       transferManager;
    private final int                                   monitorInterval;
    private final long                                  jobDbCacheTime;
    private final long                                  jobDbCacheCheckInterval;

    // job profile db is only used to recover instance which is not finished running.
    private final JobProfileDb jobConfDB;
    private final JobMetrics   jobMetrics;
    private final AtomicLong   index = new AtomicLong(0);

    /**
     * init job manager
     *
     * @param transferManager - agent manager
     */
    public JobManager(TransferManager transferManager, JobProfileDb jobConfDb) {
        this.jobConfDB = jobConfDb;
        this.transferManager = transferManager;

        // job thread pool for running
        this.runningPool = new ThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new TransferThreadFactory("job"));

        this.jobs = new ConcurrentHashMap<>();
        this.pendingJobs = new ConcurrentHashMap<>();

        TransferConfiguration conf = TransferConfiguration.getAgentConf();

        this.monitorInterval = conf.getInt(TransferConstants.JOB_MONITOR_INTERVAL, TransferConstants.DEFAULT_JOB_MONITOR_INTERVAL);
        this.jobDbCacheTime = conf.getLong(JOB_DB_CACHE_TIME, DEFAULT_JOB_DB_CACHE_TIME);
        this.jobDbCacheCheckInterval = conf.getLong(JOB_DB_CACHE_CHECK_INTERVAL, DEFAULT_JOB_DB_CACHE_CHECK_INTERVAL);
        this.jobMetrics = JobMetrics.create();
    }

    /**
     * submit job to work thread.
     *
     * @param job - job
     */
    public void addJob(Job job) {
        try {
            JobWrapper jobWrapper = new JobWrapper(transferManager, job);
            this.runningPool.execute(jobWrapper);
            JobWrapper jobWrapperRet = jobs.putIfAbsent(jobWrapper.getJob().getJobInstanceId(), jobWrapper);
            if (jobWrapperRet != null) {
                LOGGER.warn("{} has been added to running pool, "
                        + "cannot be added repeatedly", job.getJobInstanceId());
            } else {
                jobMetrics.runningJobs.incr();
            }
        } catch (Exception rje) {
            LOGGER.debug("reject job {}", job.getJobInstanceId(), rje);
            pendingJobs.putIfAbsent(job.getJobInstanceId(), job);
        }
    }

    /**
     * add job profile
     *
     * @param profile - job profile.
     */
    public boolean submitJobProfile(JobProfile profile) {
        if (profile == null || !profile.allRequiredKeyExist()) {
            LOGGER.error("profile is null or not all required key exists {}", profile == null ? null : profile.toJsonStr());
            return false;
        }
        String jobId = profile.get(JOB_ID);

        profile.set(JOB_INSTANCE_ID, TransferUtils.getUniqId(JOB_ID_PREFIX, jobId, index.incrementAndGet()));
        LOGGER.info("submit job profile {}", profile.toJsonStr());

        getJobConfDb().storeJobFirstTime(profile);
        addJob(new Job(profile));

        return true;
    }

    /**
     * delete job profile and stop job thread
     *
     * @param jobInstancId
     */
    public void deleteJob(String jobInstancId) {
        JobWrapper jobWrapper = jobs.remove(jobInstancId);
        if (jobWrapper != null) {
            LOGGER.info("delete job instance with job id {}", jobInstancId);
            jobWrapper.cleanup();
            getJobConfDb().deleteJob(jobInstancId);
        }
    }

    /**
     * start all accepted jobs.
     */
    private void startJobs() {
        List<JobProfile> profileList = getJobConfDb().getAcceptedJobs();
        for (JobProfile profile : profileList) {
            LOGGER.info("init starting job from db {}", profile.toJsonStr());
            addJob(new Job(profile));
        }
    }

    public Runnable jobStateCheckThread() {
        return () -> {
            while (isRunnable()) {
                try {
                    // check pending jobs and try to submit again.
                    for (String jobId : pendingJobs.keySet()) {
                        Job job = pendingJobs.remove(jobId);
                        if (job != null) {
                            addJob(job);
                        }
                    }
                    TimeUnit.SECONDS.sleep(monitorInterval);
                } catch (Exception ex) {
                    LOGGER.error("error caught", ex);
                }
            }
        };
    }

    /**
     * check local db and delete old tasks.
     *
     * @return
     */
    public Runnable dbStorageCheckThread() {
        return () -> {
            while (isRunnable()) {
                try {
                    jobConfDB.removeExpireJobs(jobDbCacheTime);
                    TimeUnit.SECONDS.sleep(jobDbCacheCheckInterval);
                } catch (Exception ex) {
                    LOGGER.error("error caught", ex);
                }
            }
        };
    }

    /**
     * mark job as success by job id.
     *
     * @param jobId - job id
     */
    public void markJobAsSuccess(String jobId) {
        JobWrapper wrapper = jobs.remove(jobId);
        if (wrapper != null) {
            jobMetrics.runningJobs.decr();
            LOGGER.info("job instance {} is success", jobId);
            // mark job as success.
            jobConfDB.updateJobState(jobId, StateSearchKey.SUCCESS);
        }
    }

    public void markJobAsFailed(String jobId) {
        JobWrapper wrapper = jobs.remove(jobId);
        if (wrapper != null) {
            LOGGER.info("job instance {} is failed", jobId);
            jobMetrics.runningJobs.decr();
            jobMetrics.fatalJobs.incr();
            // mark job as success.
            jobConfDB.updateJobState(jobId, StateSearchKey.FAILED);
        }
    }

    public JobProfileDb getJobConfDb() {
        return jobConfDB;
    }

    /**
     * check job existence using job file name
     *
     * @return
     */
    public boolean checkJobExsit(String fileName) {
        return jobConfDB.getJob(fileName) != null;
    }

    public Map<String, JobWrapper> getJobs() {
        return jobs;
    }

    @Override
    public void start() {
        submitWorker(jobStateCheckThread());
        submitWorker(dbStorageCheckThread());
        startJobs();
    }

    @Override
    public void stop() throws Exception {
        waitForTerminate();
        this.runningPool.shutdown();
    }
}
