package com.zmops.zeus.iot.server.storage.plugin.none;

import com.zmops.zeus.iot.server.core.storage.IRecordDAO;
import com.zmops.zeus.iot.server.core.storage.StorageDAO;

/**
 * @author nantian created at 2021/9/27 21:29
 */
public class StorageDAONoop implements StorageDAO {

    @Override
    public IRecordDAO newRecordDao() {
        return null;
    }
}
