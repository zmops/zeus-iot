package com.zmops.zeus.iot.server.transfer.core.source;


import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.api.Reader;
import com.zmops.zeus.iot.server.transfer.api.Source;
import com.zmops.zeus.iot.server.transfer.core.source.reader.TextFileReader;
import com.zmops.zeus.iot.server.transfer.core.utils.FileSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.zmops.zeus.iot.server.transfer.conf.CommonConstants.POSITION_SUFFIX;
import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.DEFAULT_JOB_LINE_FILTER;
import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.JOB_LINE_FILTER_PATTERN;

/**
 * Read text files
 */
public class TextFileSource implements Source {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileSource.class);

    // path + suffix
    public static final String MD5_SUFFIX = ".md5";

    @Override
    public List<Reader> split(JobProfile jobConf) {

        Collection<File> allFiles = FileSearchUtils.findSuitFiles(jobConf);
        List<Reader>     result   = new ArrayList<>();

        String filterPattern = jobConf.get(JOB_LINE_FILTER_PATTERN, DEFAULT_JOB_LINE_FILTER);

        for (File file : allFiles) {
            int seekPosition = jobConf.getInt(file.getAbsolutePath() + POSITION_SUFFIX, 0);

            LOGGER.info("read from history position {} with job profile {}", seekPosition, jobConf.getInstanceId());
            String md5 = jobConf.get(file.getAbsolutePath() + MD5_SUFFIX, "");

            TextFileReader textFileReader = new TextFileReader(file, seekPosition);
            addValidator(filterPattern, textFileReader);
            result.add(textFileReader);
        }

        return result;
    }


    private void addValidator(String filterPattern, TextFileReader textFileReader) {
        textFileReader.addPatternValidator(filterPattern);
    }
}
