package com.zmops.zeus.iot.server.transfer.api;

/**
 * Reader reads data and provides condition whether the reading action is finished. It's called at
 * Task level.
 */
public interface Reader extends Stage {

    /**
     * Read message
     *
     * @return - message
     */
    Message read();

    /**
     * Whether finish reading
     *
     * @return
     */
    boolean isFinished();

    /**
     * Return the reader's reading file name
     *
     * @return
     */
    String getReadFile();

    /**
     * set readTimeout
     */
    void setReadTimeout(long mill);
}
