package com.zmops.zeus.iot.server.storage.plugin.jdbc.tdengine;

import com.zmops.zeus.iot.server.core.UnexpectedException;
import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.datacarrier.DataCarrier;
import com.zmops.zeus.iot.server.datacarrier.consumer.BulkConsumePool;
import com.zmops.zeus.iot.server.datacarrier.consumer.ConsumerPoolFactory;
import com.zmops.zeus.iot.server.datacarrier.consumer.IConsumer;
import com.zmops.zeus.iot.server.library.client.jdbc.JDBCClientException;
import com.zmops.zeus.iot.server.library.client.jdbc.hikaricp.JDBCHikariCPClient;
import com.zmops.zeus.iot.server.library.client.request.InsertRequest;
import com.zmops.zeus.iot.server.library.client.request.PrepareRequest;
import com.zmops.zeus.iot.server.library.util.CollectionUtils;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.SQLExecutor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author nantian created at 2021/9/4 0:31
 */
@Slf4j
public class TDEngineBatchDAO implements IBatchDAO {

    private final JDBCHikariCPClient          tdengineClient;
    private final DataCarrier<PrepareRequest> dataCarrier;

    public TDEngineBatchDAO(JDBCHikariCPClient client) {
        this.tdengineClient = client;

        String name = "TDENGINE_ASYNCHRONOUS_BATCH_PERSISTENT";

        BulkConsumePool.Creator creator = new BulkConsumePool.Creator(name, 1, 20);
        try {
            ConsumerPoolFactory.INSTANCE.createIfAbsent(name, creator);
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage(), e);
        }

        this.dataCarrier = new DataCarrier<>(1, 10000);
        this.dataCarrier.consume(ConsumerPoolFactory.INSTANCE.get(name), new TDEngineBatchConsumer(this));
    }


    @Override
    public void flush(List<PrepareRequest> prepareRequests) {
        if (CollectionUtils.isEmpty(prepareRequests)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("batch sql statements execute, data size: {}", prepareRequests.size());
        }

        try (Connection connection = tdengineClient.getConnection()) {
            for (PrepareRequest prepareRequest : prepareRequests) {
                try {
                    SQLExecutor sqlExecutor = (SQLExecutor) prepareRequest;
                    sqlExecutor.invoke(connection);
                } catch (SQLException e) {
                    // Just avoid one execution failure makes the rest of batch failure.
                    log.error(e.getMessage(), e);
                }
            }
        } catch (SQLException | JDBCClientException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void insert(InsertRequest insertRequest) {
        this.dataCarrier.produce(insertRequest);
    }

    private static class TDEngineBatchConsumer implements IConsumer<PrepareRequest> {

        private final TDEngineBatchDAO tdengineBatchDAO;

        private TDEngineBatchConsumer(TDEngineBatchDAO h2BatchDAO) {
            this.tdengineBatchDAO = h2BatchDAO;
        }

        @Override
        public void init() {

        }

        @Override
        public void consume(List<PrepareRequest> prepareRequests) {
            tdengineBatchDAO.flush(prepareRequests);
        }

        @Override
        public void onError(List<PrepareRequest> prepareRequests, Throwable t) {
            log.error(t.getMessage(), t);
        }

        @Override
        public void onExit() {
        }
    }
}
