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

package com.zmops.zeus.iot.server.transfer.core.utils;

import com.zmops.zeus.iot.server.transfer.conf.JobConstants;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.conf.TriggerProfile;
import com.zmops.zeus.iot.server.transfer.core.trigger.PathPattern;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.zmops.zeus.iot.server.transfer.conf.CommonConstants.DEFAULT_FILE_MAX_NUM;
import static com.zmops.zeus.iot.server.transfer.conf.CommonConstants.FILE_MAX_NUM;
import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.JOB_DIR_FILTER_PATTERN;
import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.JOB_RETRY_TIME;

/**
 * Utils for plugin package.
 */
@Slf4j
public class FileSearchUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSearchUtils.class);

    public static Collection<File> findSuitFiles(JobProfile jobConf) {
        String dirPattern = jobConf.get(JOB_DIR_FILTER_PATTERN);
        LOGGER.info("start to find files with dir pattern {}", dirPattern);

        PathPattern pattern = new PathPattern(dirPattern);
        updateRetryTime(jobConf, pattern);

        int maxFileNum = jobConf.getInt(FILE_MAX_NUM, DEFAULT_FILE_MAX_NUM);
        LOGGER.info("dir pattern {}, max file num {}", dirPattern, maxFileNum);

        Collection<File> allFiles = new ArrayList<>();
        try {
            pattern.walkAllSuitableFiles(allFiles, maxFileNum);
        } catch (IOException ex) {
            LOGGER.warn("cannot get all files from {}", dirPattern, ex);
        }
        return allFiles;
    }

    /**
     * if the job is retry job, the date is determined
     *
     * @param jobConf
     * @param pattern
     */
    public static void updateRetryTime(JobProfile jobConf, PathPattern pattern) {
        if (jobConf.hasKey(JOB_RETRY_TIME)) {
            LOGGER.info("job {} is retry job with specific time, update file time to {}" + "", jobConf.toJsonStr(), jobConf.get(JOB_RETRY_TIME));
            pattern.updateDateFormatRegex(jobConf.get(JOB_RETRY_TIME));
        }
    }

    public static JobProfile copyJobProfile(TriggerProfile triggerProfile, String dataTime, File pendingFile) {
        JobProfile copiedProfile = TriggerProfile.parseJsonStr(triggerProfile.toJsonStr());

        String md5 = TransferUtils.getFileMd5(pendingFile);

        copiedProfile.set(pendingFile.getAbsolutePath() + ".md5", md5);
        copiedProfile.set(JobConstants.JOB_DIR_FILTER_PATTERN, pendingFile.getAbsolutePath());

        // the time suit for file name is just the data time
        copiedProfile.set(JobConstants.JOB_DATA_TIME, dataTime);
        return copiedProfile;
    }

}
