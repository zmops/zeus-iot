package com.zmops.iot.web.event;

/**
 * @author yefei
 **/
public interface EventProcess {

    void process(String triggerId,String triggerName);

    boolean checkTag(String tag);
}
