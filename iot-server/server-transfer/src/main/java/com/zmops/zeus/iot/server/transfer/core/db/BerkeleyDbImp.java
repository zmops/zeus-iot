package com.zmops.zeus.iot.server.transfer.core.db;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.*;
import com.zmops.zeus.iot.server.transfer.conf.CommonConstants;
import com.zmops.zeus.iot.server.transfer.conf.TransferConfiguration;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.core.job.CommandEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * DB implement based on berkeley db.
 */
public class BerkeleyDbImp implements Db {

    private static final Logger LOGGER = LoggerFactory.getLogger(BerkeleyDbImp.class);

    private final EntityStore jobStore;
    private final EntityStore commandStore;

    private final PrimaryIndex<String, KeyValueEntity>                   primaryIndex;
    private final SecondaryIndex<StateSearchKey, String, KeyValueEntity> secondaryIndex;
    private final PrimaryIndex<String, CommandEntity>                    commandPrimaryIndex;

    private final SecondaryIndex<String, String, KeyValueEntity> fileNameSecondaryIndex;
    private final SecondaryIndex<Boolean, String, CommandEntity> commandSecondaryIndex;

    private final TransferConfiguration transferConfig;

    public BerkeleyDbImp() {

        this.transferConfig = TransferConfiguration.getAgentConf();

        StoreConfig storeConfig = initStoreConfig();
        Environment environment = initEnv();

        String instanceName = transferConfig.get(TransferConstants.AGENT_DB_INSTANCE_NAME, TransferConstants.DEFAULT_AGENT_DB_INSTANCE_NAME);

        this.jobStore = new EntityStore(environment, instanceName, storeConfig);
        this.commandStore = new EntityStore(environment, CommonConstants.COMMAND_STORE_INSTANCE_NAME, storeConfig);

        commandPrimaryIndex = this.commandStore.getPrimaryIndex(String.class, CommandEntity.class);

        commandSecondaryIndex = commandStore.getSecondaryIndex(commandPrimaryIndex, Boolean.class, "isAcked");

        primaryIndex = this.jobStore.getPrimaryIndex(String.class, KeyValueEntity.class);
        secondaryIndex = this.jobStore.getSecondaryIndex(primaryIndex, StateSearchKey.class, "stateSearchKey");

        fileNameSecondaryIndex = this.jobStore.getSecondaryIndex(primaryIndex, String.class, "fileName");
    }

    /**
     * init store by config
     *
     * @return store config
     */
    private StoreConfig initStoreConfig() {
        return new StoreConfig()
                .setReadOnly(transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_READONLY, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_READONLY))
                .setAllowCreate(!transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_READONLY, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_READONLY))
                .setTransactional(transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_TRANSACTIONAL, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_TRANSACTIONAL));
    }

    /**
     * init local bdb path and get it.
     *
     * @return local path.
     */
    private File tryToInitAndGetPath() {
        String storePath  = transferConfig.get(TransferConstants.AGENT_LOCAL_STORE_PATH, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_PATH);
        String parentPath = transferConfig.get(TransferConstants.AGENT_HOME, TransferConstants.DEFAULT_AGENT_HOME);

        File finalPath = new File(parentPath, storePath);
        try {
            boolean result = finalPath.mkdirs();
            LOGGER.info("try to create local path {}, result is {}", finalPath, result);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return finalPath;
    }

    /**
     * init env by config
     *
     * @return env config
     */
    private Environment initEnv() {
        EnvironmentConfig envConfig = new EnvironmentConfig()
                .setReadOnly(transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_READONLY, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_READONLY))
                .setAllowCreate(!transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_READONLY, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_READONLY))
                .setTransactional(transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_TRANSACTIONAL, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_TRANSACTIONAL))
                .setLockTimeout(transferConfig.getInt(TransferConstants.AGENT_LOCAL_STORE_LOCK_TIMEOUT, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_LOCK_TIMEOUT), TimeUnit.MILLISECONDS);

        envConfig.setTxnNoSyncVoid(transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_NO_SYNC_VOID, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_NO_SYNC_VOID));
        envConfig.setTxnWriteNoSyncVoid(transferConfig.getBoolean(TransferConstants.AGENT_LOCAL_STORE_WRITE_NO_SYNC_VOID, TransferConstants.DEFAULT_AGENT_LOCAL_STORE_WRITE_NO_SYNC_VOID));
        return new Environment(tryToInitAndGetPath(), envConfig);
    }

    @Override
    public KeyValueEntity get(String key) {
        requireNonNull(key);
        return primaryIndex.get(key);
    }


    @Override
    public CommandEntity getCommand(String commandId) {
        requireNonNull(commandId);
        return commandPrimaryIndex.get(commandId);
    }


    @Override
    public CommandEntity putCommand(CommandEntity entity) {
        requireNonNull(entity);
        return commandPrimaryIndex.put(entity);
    }

    @Override
    public void set(KeyValueEntity entity) {
        requireNonNull(entity);
        primaryIndex.put(entity);
    }

    @Override
    public KeyValueEntity put(KeyValueEntity entity) {
        requireNonNull(entity);
        return primaryIndex.put(entity);
    }

    @Override
    public KeyValueEntity remove(String key) {
        requireNonNull(key);
        KeyValueEntity entity = primaryIndex.get(key);
        primaryIndex.delete(key);
        return entity;
    }

    @Override
    public List<KeyValueEntity> search(StateSearchKey searchKey) {
        requireNonNull(searchKey);
        List<KeyValueEntity> ret = new ArrayList<>();

        try (EntityCursor<KeyValueEntity> children = secondaryIndex.subIndex(searchKey).entities()) {
            for (KeyValueEntity entity : children) {
                ret.add(entity);
            }
        }
        return ret;
    }

    @Override
    public List<CommandEntity> searchCommands(boolean isAcked) {
        requireNonNull(isAcked);
        List<CommandEntity> ret = new ArrayList<>();
        try (EntityCursor<CommandEntity> children = commandSecondaryIndex.subIndex(isAcked).entities()) {
            for (CommandEntity entity : children) {
                ret.add(entity);
            }
        }
        return ret;
    }

    @Override
    public KeyValueEntity searchOne(StateSearchKey searchKey) {
        requireNonNull(searchKey);
        return secondaryIndex.get(searchKey);
    }

    @Override
    public KeyValueEntity searchOne(String fileName) {
        requireNonNull(fileName);
        return fileNameSecondaryIndex.get(fileName);
    }

    @Override
    public List<KeyValueEntity> findAll(String prefix) {
        requireNonNull(prefix);
        List<KeyValueEntity> ret = new ArrayList<>();
        try (EntityCursor<KeyValueEntity> children = primaryIndex.entities()) {
            for (KeyValueEntity entity : children) {
                if (entity.getKey().startsWith(prefix)) {
                    ret.add(entity);
                }
            }
        }
        return ret;
    }

    @Override
    public void close() {
        jobStore.close();
    }
}
