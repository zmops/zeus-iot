package com.zmops.zeus.iot.server.transfer.conf;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * agent configuration. Only one instance in the process.
 * Basically it use properties file to store configurations.
 */
public class TransferConfiguration extends AbstractConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferConfiguration.class);

    private static final String DEFAULT_CONFIG_FILE = "tansfer.properties";
    private static final String TMP_CONFIG_FILE = ".tmp.agent.properties";

    private static final ArrayList<String> LOCAL_RESOURCES = new ArrayList<>();

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    static {
        LOCAL_RESOURCES.add(DEFAULT_CONFIG_FILE);
    }

    private static volatile TransferConfiguration transferConfig = null;

    /**
     * load config from agent file.
     */
    private TransferConfiguration() {
        for (String fileName : LOCAL_RESOURCES) {
            super.loadPropertiesResource(fileName);
        }
    }

    /**
     * singleton for agent configuration.
     *
     * @return - static instance of AgentConfiguration
     */
    public static TransferConfiguration getAgentConf() {
        if (transferConfig == null) {
            synchronized (TransferConfiguration.class) {
                if (transferConfig == null) {
                    transferConfig = new TransferConfiguration();
                }
            }
        }
        return transferConfig;
    }

    private String getNextBackupFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = format.format(new Date(System.currentTimeMillis()));
        return DEFAULT_CONFIG_FILE + "." + dateStr;
    }

    /**
     * flush config to local files.
     */
    public void flushToLocalPropertiesFile() {
        LOCK.writeLock().lock();
        // TODO: flush to local file as properties file.
        try {
            String agentConfParent = get(TransferConstants.AGENT_CONF_PARENT, TransferConstants.DEFAULT_AGENT_CONF_PARENT);

            File sourceFile = new File(agentConfParent, DEFAULT_CONFIG_FILE);
            File targetFile = new File(agentConfParent, getNextBackupFileName());
            File tmpFile = new File(agentConfParent, TMP_CONFIG_FILE);

            if (sourceFile.exists()) {
                FileUtils.copyFile(sourceFile, targetFile);
            }

            List<String> tmpCache = getStorageList();
            FileUtils.writeLines(tmpFile, tmpCache);

            FileUtils.copyFile(tmpFile, sourceFile);
            boolean result = tmpFile.delete();
            if (!result) {
                LOGGER.warn("cannot delete file {}", tmpFile);
            }
        } catch (Exception ex) {
            LOGGER.error("error while flush agent conf to local", ex);
        } finally {
            LOCK.writeLock().unlock();
        }

    }

    @Override
    public boolean allRequiredKeyExist() {
        return true;
    }
}
