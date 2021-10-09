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
import com.zmops.zeus.iot.server.storage.plugin.jdbc.SQLBuilder;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.SQLExecutor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nantian created at 2021/9/4 0:31
 */
@Slf4j
public class TDEngineBatchDAO implements IBatchDAO {

    private final JDBCHikariCPClient tdengineClient;
    private final DataCarrier<PrepareRequest> dataCarrier;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public TDEngineBatchDAO(JDBCHikariCPClient client) {
        this.tdengineClient = client;

        String name = "TDENGINE_ASYNCHRONOUS_BATCH_PERSISTENT";

        BulkConsumePool.Creator creator = new BulkConsumePool.Creator(name, 1, 20);
        try {
            ConsumerPoolFactory.INSTANCE.createIfAbsent(name, creator);
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage(), e);
        }

        this.dataCarrier = new DataCarrier<>(4, 2500);
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

        SQLBuilder execSql = new SQLBuilder(" INSERT INTO ");
        for (PrepareRequest prepareRequest : prepareRequests) {
            SQLExecutor sqlExecutor = (SQLExecutor) prepareRequest;
            execSql.appendLine(sqlExecutor.getSql());
        }
        execSql.append(";");

        executor.submit(() -> {
            PreparedStatement preparedStatement;
            Connection connection = null;
            try {
                connection = tdengineClient.getConnection();
                preparedStatement = connection.prepareStatement(execSql.toString());
                preparedStatement.execute();
            } catch (SQLException | JDBCClientException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


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
