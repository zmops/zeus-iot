package com.zmops.zeus.iot.server.core.worker;

import com.google.gson.Gson;
import com.zmops.zeus.iot.server.core.UnexpectedException;
import com.zmops.zeus.iot.server.core.worker.data.ItemValue;
import com.zmops.zeus.iot.server.core.worker.data.ZabbixTrapper;
import com.zmops.zeus.iot.server.datacarrier.DataCarrier;
import com.zmops.zeus.iot.server.datacarrier.consumer.BulkConsumePool;
import com.zmops.zeus.iot.server.datacarrier.consumer.ConsumerPoolFactory;
import com.zmops.zeus.iot.server.datacarrier.consumer.IConsumer;
import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import com.zmops.zeus.iot.server.sender.service.ZabbixSenderService;
import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import com.zmops.zeus.iot.server.telemetry.api.CounterMetrics;
import com.zmops.zeus.iot.server.telemetry.api.MetricsCreator;
import com.zmops.zeus.iot.server.telemetry.api.MetricsTag;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author nantian created at 2021/8/23 15:11
 * <p>
 * 数据发送队列
 */
@Slf4j
public class ItemDataTransferWorker extends TransferWorker<ItemValue> {


    private final DataCarrier<ItemValue> dataCarrier;
    private final CounterMetrics         iotDataTransferCounter;
    private final Gson                   gson = new Gson();

    public ItemDataTransferWorker(ModuleManager moduleManager) {
        super(moduleManager);

        String name = "ITEMVALUE_TRANSFER_TUNNEL";

        int size = BulkConsumePool.Creator.recommendMaxSize() / 8;
        if (size == 0) {
            size = 1;
        }
        BulkConsumePool.Creator creator = new BulkConsumePool.Creator(name, size, 20);
        try {
            ConsumerPoolFactory.INSTANCE.createIfAbsent(name, creator);
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage(), e);
        }

        this.dataCarrier = new DataCarrier<>("ZABBIX_SENDER", name, 4, 2000);
        this.dataCarrier.consume(ConsumerPoolFactory.INSTANCE.get(name), new SenderConsumer());

        // #### 发送数量 指标采集
        MetricsCreator metricsCreator = moduleManager.find(TelemetryModule.NAME)
                .provider()
                .getService(MetricsCreator.class);

        iotDataTransferCounter = metricsCreator.createCounter(
                "transfer_data_count", "The count number of iot device data in transfer",
                new MetricsTag.Keys("name"),
                new MetricsTag.Values("transfer_data_count")
        );
    }


    @Override
    public void in(ItemValue itemValue) {
        iotDataTransferCounter.inc();
        dataCarrier.produce(itemValue);
    }

    @Override
    public void prepareBatch(Collection<ItemValue> lastCollection) {
        long start = System.currentTimeMillis();
        if (lastCollection.size() == 0) {
            return;
        }

        int             maxBatchGetSize = 500;
        final int       batchSize       = Math.min(maxBatchGetSize, lastCollection.size());
        List<ItemValue> valueList       = new ArrayList<>();

        for (ItemValue data : lastCollection) {
            valueList.add(data);
            if (valueList.size() == batchSize) {
                batchSenderDataToZabbix(valueList);
            }
        }

        if (valueList.size() > 0) {
            batchSenderDataToZabbix(valueList);
        }

        log.debug("batch sender data size：{}, took time: {}", lastCollection.size(), System.currentTimeMillis() - start);
    }


    private void batchSenderDataToZabbix(List<ItemValue> valueList) {
        ZabbixTrapper zabbixTrapper = new ZabbixTrapper(valueList);

        ZabbixSenderService senderService = getModuleDefineHolder()
                .find(ZabbixSenderModule.NAME).provider().getService(ZabbixSenderService.class);

        try {
            String sendResult = senderService.sendData(gson.toJson(zabbixTrapper));
            log.debug(sendResult);
        } catch (IOException e) {
            log.error(" itemvalue data sender error，msg ：{}", e.getMessage());
            e.printStackTrace();
        } finally {
            valueList.clear();
        }
    }


    private class SenderConsumer implements IConsumer<ItemValue> {
        @Override
        public void init() {

        }

        @Override
        public void consume(List<ItemValue> data) {
            ItemDataTransferWorker.this.onWork(data);
        }

        @Override
        public void onError(List<ItemValue> data, Throwable t) {
            log.error(t.getMessage(), t);
        }

        @Override
        public void onExit() {
        }
    }


}
