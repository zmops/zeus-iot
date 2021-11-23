package com.zmops.zeus.iot.server.core.analysis.worker;

import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.core.storage.IRecordDAO;
import com.zmops.zeus.iot.server.core.storage.StorageModule;
import com.zmops.zeus.iot.server.core.storage.model.Model;
import com.zmops.zeus.iot.server.core.worker.AbstractWorker;
import com.zmops.zeus.iot.server.client.request.InsertRequest;
import com.zmops.zeus.server.library.module.ModuleDefineHolder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class RecordPersistentWorker extends AbstractWorker<Record> {

    private final Model model;
    private final IRecordDAO recordDAO;
    private final IBatchDAO batchDAO;

    public RecordPersistentWorker(ModuleDefineHolder moduleDefineHolder, Model model, IRecordDAO recordDAO) {
        super(moduleDefineHolder);
        this.model = model;
        this.recordDAO = recordDAO;
        this.batchDAO = moduleDefineHolder.find(StorageModule.NAME).provider().getService(IBatchDAO.class);
    }

    @Override
    public void in(Record record) {
        try {
            InsertRequest insertRequest = recordDAO.prepareBatchInsert(model, record);
            batchDAO.insert(insertRequest);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
