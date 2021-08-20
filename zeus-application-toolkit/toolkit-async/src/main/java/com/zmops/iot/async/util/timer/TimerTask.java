package com.zmops.iot.async.util.timer;

/**
 * 类似于netty的TimerTask。
 *
 * @author create by TcSnZh on 2021/5/9-下午5:17
 */
public interface TimerTask {
    void run(Timeout timeout) throws Exception;
}
