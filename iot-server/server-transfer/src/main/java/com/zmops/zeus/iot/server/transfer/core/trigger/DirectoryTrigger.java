package com.zmops.zeus.iot.server.transfer.core.trigger;

import com.zmops.zeus.iot.server.transfer.conf.JobConstants;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.conf.TriggerProfile;
import com.zmops.zeus.iot.server.transfer.core.api.Trigger;
import com.zmops.zeus.iot.server.transfer.core.common.AbstractDaemon;
import com.zmops.zeus.iot.server.transfer.core.utils.FileSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


/**
 * Watch directory, if new valid files are created, create
 * jobs correspondingly.
 */
public class DirectoryTrigger extends AbstractDaemon implements Trigger {

    private static final    Logger       LOGGER = LoggerFactory.getLogger(DirectoryTrigger.class);
    private static volatile WatchService watchService;

    private final ConcurrentHashMap<PathPattern, List<WatchKey>> allWatchers = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<JobProfile>                queue       = new LinkedBlockingQueue<>();

    private TriggerProfile profile;
    private int            interval;

    private static void initWatchService() {
        try {
            if (watchService == null) {
                synchronized (DirectoryTrigger.class) {
                    if (watchService == null) {
                        watchService = FileSystems.getDefault().newWatchService();
                        LOGGER.info("init watch service {}", watchService);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("error while init watch service", ex);
        }
    }

    public TriggerProfile getProfile() {
        return profile;
    }

    @Override
    public void destroy() {
        try {
            stop();
        } catch (Exception ex) {
            LOGGER.error("exception while stopping threads", ex);
        }
    }

    @Override
    public JobProfile fetchJobProfile() {
        return queue.poll();
    }

    @Override
    public TriggerProfile getTriggerProfile() {
        return profile;
    }

    @Override
    public void stop() {
        waitForTerminate();
        releaseResource();
    }

    /**
     * register all sub-directory
     *
     * @param entity      - entity
     * @param path        - path
     * @param tmpWatchers - watchers
     */
    private void registerAllSubDir(PathPattern entity,
                                   Path path,
                                   List<WatchKey> tmpWatchers) throws Exception {
        // check regex
        LOGGER.info("check whether path {} is suitable", path);

        if (entity.suitForWatch(path.toString())) {

            if (path.toFile().isDirectory()) {
                WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                tmpWatchers.add(watchKey);

                try (Stream<Path> stream = Files.list(path)) {
                    Iterator<Path> iterator = stream.iterator();
                    while (iterator.hasNext()) {
                        registerAllSubDir(entity, iterator.next().toAbsolutePath(), tmpWatchers);
                    }
                }

            } else {
                JobProfile copiedJobProfile = FileSearchUtils.copyJobProfile(profile, entity.getSuitTime(), path.toFile());
                LOGGER.info("trigger {} generate job profile to read file {}", getTriggerProfile().getTriggerId(), path.toString());
                queue.offer(copiedJobProfile);
            }
        }
    }

    /**
     * if directory has created, then check whether directory is valid
     *
     * @param entity      - entity
     * @param watchKey    - watch key
     * @param tmpWatchers - watchers
     */
    private void registerNewDir(PathPattern entity, WatchKey watchKey, List<WatchKey> tmpWatchers, List<WatchKey> tmpDeletedWatchers) throws Exception {

        Path parentPath = (Path) watchKey.watchable();

        for (WatchEvent<?> event : watchKey.pollEvents()) {
            // if watch event is too much, then event would be overflow.
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                // only watch create event, so else is create-event.
                entity.updateDateFormatRegex();
                Path createdPath = (Path) event.context();
                if (createdPath != null) {
                    registerAllSubDir(entity, parentPath.resolve(createdPath), tmpWatchers);
                }
            } else if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                LOGGER.info("overflow got {}", parentPath);
                // check whether parent path is valid.
                if (Files.isDirectory(parentPath)) {
                    for (Iterator<Path> it = Files.list(parentPath).iterator(); it.hasNext(); ) {
                        Path childPath = it.next();
                        registerAllSubDir(entity, parentPath.resolve(childPath), tmpWatchers);
                    }
                }
            }
        }
        if (!Files.exists(parentPath)) {
            LOGGER.warn("{} not exist, add watcher to pending delete list", parentPath);
            tmpDeletedWatchers.add(watchKey);
        }
    }

    /**
     * handler watchers
     *
     * @return runnable
     */
    private Runnable watchEventHandler() {
        return () -> {
            while (isRunnable()) {
                try {
                    TimeUnit.SECONDS.sleep(interval);
                    allWatchers.forEach((pathPattern, watchKeys) -> {
                        List<WatchKey> tmpWatchers        = new ArrayList<>();
                        List<WatchKey> tmpDeletedWatchers = new ArrayList<>();

                        pathPattern.cleanup();
                        try {
                            for (WatchKey watchKey : watchKeys) {
                                registerNewDir(pathPattern, watchKey, tmpWatchers, tmpDeletedWatchers);
                            }
                        } catch (Exception ex) {
                            LOGGER.error("error caught", ex);
                        }
                        watchKeys.addAll(tmpWatchers);
                        watchKeys.removeAll(tmpDeletedWatchers);
                    });
                } catch (Exception ex) {
                    LOGGER.error("error caught", ex);
                }
            }
        };
    }

    private void releaseResource() {
        allWatchers.forEach((absoluteFilePath, watchKeys) -> {
            watchKeys.forEach(WatchKey::cancel);
        });
        allWatchers.clear();
    }

    @Override
    public void start() throws Exception {
        submitWorker(watchEventHandler());
    }

    public void register(String pathPattern) throws IOException {
        PathPattern entity = new PathPattern(pathPattern);
        innerRegister(pathPattern, entity);
    }

    public void register(String pathPattern, String offset) throws IOException {
        PathPattern entity = new PathPattern(pathPattern, offset);
        innerRegister(pathPattern, entity);
    }

    private void innerRegister(String pathPattern, PathPattern entity) throws IOException {
        List<WatchKey> tmpKeyList = new ArrayList<>();
        List<WatchKey> keyList    = allWatchers.putIfAbsent(entity, tmpKeyList);

        if (keyList == null) {
            Path rootPath = Paths.get(entity.getRootDir());
            LOGGER.info("watch root path is {}", rootPath);
            WatchKey key = rootPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            tmpKeyList.add(key);
        } else {
            LOGGER.error("{} exists in watcher list, please check it", pathPattern);
        }
    }

    public void unregister(String pathPattern) {
        PathPattern          entity  = new PathPattern(pathPattern);
        Collection<WatchKey> allKeys = allWatchers.remove(entity);
        if (allKeys != null) {
            LOGGER.info("unregister pattern {}, total size of path {}", pathPattern, allKeys.size());
            for (WatchKey key : allKeys) {
                key.cancel();
            }
        }
    }

    ConcurrentHashMap<PathPattern, List<WatchKey>> getAllWatchers() {
        return allWatchers;
    }

    @Override
    public void init(TriggerProfile profile) throws IOException {
        initWatchService();
        interval = profile.getInt(TransferConstants.TRIGGER_CHECK_INTERVAL, TransferConstants.DEFAULT_TRIGGER_CHECK_INTERVAL);
        this.profile = profile;

        if (this.profile.hasKey(JobConstants.JOB_DIR_FILTER_PATTERN)) {
            String pathPattern = this.profile.get(JobConstants.JOB_DIR_FILTER_PATTERN);
            String timeOffset  = this.profile.get(JobConstants.JOB_FILE_TIME_OFFSET, "");
            if (timeOffset.isEmpty()) {
                register(pathPattern);
            }
            register(pathPattern, timeOffset);
        }
    }

    @Override
    public void run() {
        try {
            start();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
