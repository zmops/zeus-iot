/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zmops.zeus.iot.server.transfer.conf;

public class CommonConstants {

    public static final String PROXY_BID       = "proxy.bid";
    public static final String POSITION_SUFFIX = ".position";

    public static final String PROXY_TID = "proxy.tid";

    public static final String PROXY_MAX_SENDER_PER_BID = "proxy.max.sender.per.pid";

    public static final int DEFAULT_PROXY_MAX_SENDER_PER_PID = 10;

    // max size of message list
    public static final String PROXY_PACKAGE_MAX_SIZE = "proxy.package.maxSize";

    // max size of single batch in bytes, default is 200KB.
    public static final int DEFAULT_PROXY_PACKAGE_MAX_SIZE = 2000000;

    public static final String PROXY_TID_QUEUE_MAX_NUMBER = "proxy.tid.queue.maxNumber";

    public static final int DEFAULT_PROXY_TID_QUEUE_MAX_NUMBER = 10000;

    public static final String PROXY_PACKAGE_MAX_TIMEOUT_MS = "proxy.package.maxTimeout.ms";

    public static final int DEFAULT_PROXY_PACKAGE_MAX_TIMEOUT_MS = 4 * 1000;

    public static final String PROXY_BATCH_FLUSH_INTERVAL = "proxy.batch.flush.interval";

    public static final int DEFAULT_PROXY_BATCH_FLUSH_INTERVAL = 100;

    public static final String PROXY_SENDER_MAX_TIMEOUT = "proxy.sender.maxTimeout";

    // max timeout in seconds.
    public static final int DEFAULT_PROXY_SENDER_MAX_TIMEOUT = 20;

    public static final String PROXY_SENDER_MAX_RETRY = "proxy.sender.maxRetry";

    public static final int DEFAULT_PROXY_SENDER_MAX_RETRY = 5;

    public static final String  PROXY_IS_FILE   = "proxy.isFile";
    public static final boolean DEFAULT_IS_FILE = false;

    public static final String PROXY_RETRY_SLEEP = "proxy.retry.sleep";

    public static final long DEFAULT_PROXY_RETRY_SLEEP = 500;

    public static final String PROXY_KEY_BID      = "bid";
    public static final String PROXY_KEY_TID      = "tid";
    public static final String PROXY_KEY_ID       = "id";
    public static final String PROXY_KEY_AGENT_IP = "agentip";
    public static final String PROXY_OCEANUS_F    = "f";
    public static final String PROXY_OCEANUS_BL   = "bl";


    public static final String FILE_MAX_NUM = "file.max.num";

    public static final int DEFAULT_FILE_MAX_NUM = 4096;

    public static final String TRIGGER_ID_PREFIX = "trigger_";

    public static final String COMMAND_STORE_INSTANCE_NAME = "commandStore";

    public static final String AGENT_OS_NAME = "os.name";
    public static final String AGENT_NIX_OS  = "nix";
    public static final String AGENT_NUX_OS  = "nux";
    public static final String AGENT_COLON   = ":";

}
