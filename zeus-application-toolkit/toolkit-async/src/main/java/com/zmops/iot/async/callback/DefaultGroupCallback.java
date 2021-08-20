package com.zmops.iot.async.callback;


import com.zmops.iot.async.wrapper.WorkerWrapper;

import java.util.List;

/**
 * @author wuweifeng wrote on 2019-12-27
 * @version 1.0
 * @deprecated deprecate at version 1.5.1 , see {@link IGroupCallback} .
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public class DefaultGroupCallback implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper> workerWrappers) {
        // do nothing
    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {
        // do nothing
    }
}
