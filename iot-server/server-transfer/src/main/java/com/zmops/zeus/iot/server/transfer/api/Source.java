package com.zmops.zeus.iot.server.transfer.api;

import com.zmops.zeus.iot.server.transfer.conf.JobProfile;

import java.util.List;

/**
 * Source can be split into multiple reader.
 */
public interface Source {

    /**
     * Split source into a list of readers.
     *
     * @param conf job conf
     * @return - list of reader
     */
    List<Reader> split(JobProfile conf);
}
