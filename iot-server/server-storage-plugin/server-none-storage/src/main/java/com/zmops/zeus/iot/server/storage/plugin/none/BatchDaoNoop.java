package com.zmops.zeus.iot.server.storage.plugin.none;

import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.library.client.request.InsertRequest;
import com.zmops.zeus.iot.server.library.client.request.PrepareRequest;

import java.util.List;

/**
 * @author nantian created at 2021/9/27 21:28
 */
public class BatchDaoNoop implements IBatchDAO {

    @Override
    public void insert(InsertRequest insertRequest) {

    }

    @Override
    public void flush(List<PrepareRequest> prepareRequests) {

    }
}
