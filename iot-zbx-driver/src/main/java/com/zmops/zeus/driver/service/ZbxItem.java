package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;

/**
 * @author nantian created at 2021/8/4 18:07
 */

@BaseRequest(baseURL = "${zbxApiUrl}")
public interface ZbxItem {

    String createTrapperItem()
}
