package com.zmops.zeus.iot.server.core.analysis.worker;

import com.zmops.zeus.iot.server.core.analysis.Stream;
import com.zmops.zeus.iot.server.core.analysis.StreamProcessor;
import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.storage.IRecordDAO;
import com.zmops.zeus.iot.server.core.storage.StorageDAO;
import com.zmops.zeus.iot.server.core.storage.StorageModule;
import com.zmops.zeus.iot.server.core.storage.model.Model;
import com.zmops.zeus.iot.server.library.module.ModuleDefineHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nantian created at 2021/9/6 16:47
 */
public class RecordStreamProcessor implements StreamProcessor<Record> {

    private final Map<Class<? extends Record>, RecordPersistentWorker> workers = new HashMap<>();

    private final static RecordStreamProcessor PROCESSOR = new RecordStreamProcessor();

    public static RecordStreamProcessor getInstance() {
        return PROCESSOR;
    }

    @Override
    public void in(Record record) {
        RecordPersistentWorker worker = workers.get(record.getClass());
        if (worker != null) {
            worker.in(record);
        }
    }

    public void create(ModuleDefineHolder moduleDefineHolder, Stream stream, Class<? extends Record> recordClass) {

        StorageDAO storageDAO = moduleDefineHolder.find(StorageModule.NAME).provider().getService(StorageDAO.class);
        IRecordDAO recordDAO = storageDAO.newRecordDao();

        Model model = new Model(stream.name());

        RecordPersistentWorker persistentWorker = new RecordPersistentWorker(moduleDefineHolder, model, recordDAO);

        workers.put(recordClass, persistentWorker);
    }
}
