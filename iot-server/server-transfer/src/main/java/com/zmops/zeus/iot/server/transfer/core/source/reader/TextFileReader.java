package com.zmops.zeus.iot.server.transfer.core.source.reader;

import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.api.Message;
import com.zmops.zeus.iot.server.transfer.api.Reader;
import com.zmops.zeus.iot.server.transfer.api.Validator;
import com.zmops.zeus.iot.server.transfer.core.FileException;
import com.zmops.zeus.iot.server.transfer.core.message.DefaultMessage;
import com.zmops.zeus.iot.server.transfer.metrics.PluginMetric;
import com.zmops.zeus.iot.server.transfer.core.utils.TransferUtils;
import com.zmops.zeus.iot.server.transfer.core.validator.PatternValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.DEFAULT_JOB_FILE_MAX_WAIT;
import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.JOB_FILE_MAX_WAIT;

/**
 * ndjson 文件读取
 *
 * @editor nantian
 */
public class TextFileReader implements Reader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileReader.class);

    public static final int NEVER_STOP_SIGN = -1;

    private final File   file;
    private final int    position;
    private final String md5;

    private Iterator<String> iterator;
    private Stream<String>   stream;

    private long timeout;
    private long lastTime = 0;

    private final PluginMetric    textFileMetric;
    private final List<Validator> validators = new ArrayList<>();

    public TextFileReader(File file, int position) {
        this(file, position, "");
    }

    public TextFileReader(File file, int position, String md5) {
        this.file = file;
        this.position = position;
        this.md5 = md5;
        textFileMetric = new PluginMetric();
        textFileMetric.tagName.setName(file.getAbsolutePath());
    }

    public TextFileReader(File file) {
        this(file, 0);
    }

    @Override
    public Message read() {
        if (iterator != null && iterator.hasNext()) {
            String message = iterator.next();
            if (validateMessage(message)) {
                textFileMetric.readNum.incr();
                return new DefaultMessage(message.getBytes(StandardCharsets.UTF_8));
            }
        }
        return null;
    }


    private boolean validateMessage(String message) {
        if (validators.isEmpty()) {
            return true;
        }
        return validators.stream().allMatch(v -> v.validate(message));
    }

    @Override
    public boolean isFinished() {
        if (timeout == NEVER_STOP_SIGN) {
            return false;
        }
        if (iterator == null) {
            return true;
        }
        if (iterator.hasNext()) {
            lastTime = 0;
            return false;
        } else {
            if (lastTime == 0) {
                lastTime = System.currentTimeMillis();
            }
            return System.currentTimeMillis() - lastTime > timeout;
        }
    }

    @Override
    public String getReadFile() {
        return file.getAbsolutePath();
    }

    @Override
    public void setReadTimeout(long millis) {
        timeout = millis;
    }

    public void addPatternValidator(String pattern) {
        if (pattern.isEmpty()) {
            return;
        }
        validators.add(new PatternValidator(pattern));
    }


    @Override
    public void init(JobProfile jobConf) {
        try {
            initReadTimeout(jobConf);
            String md5 = TransferUtils.getFileMd5(file);

            if (StringUtils.isNotBlank(this.md5) && !this.md5.equals(md5)) {
                LOGGER.warn("md5 is differ from origin, origin: {}, new {}", this.md5, md5);
            }

            LOGGER.info("file name for task is {}, md5 is {}", file, md5);

            stream = Files.newBufferedReader(file.toPath()).lines().skip(position);
            iterator = stream.iterator();
        } catch (Exception ex) {
            throw new FileException("error init stream for " + file.getPath(), ex);
        }
    }

    private void initReadTimeout(JobProfile jobConf) {
        int waitTime = jobConf.getInt(JOB_FILE_MAX_WAIT, DEFAULT_JOB_FILE_MAX_WAIT);
        if (waitTime == NEVER_STOP_SIGN) {
            timeout = NEVER_STOP_SIGN;
        } else {
            timeout = TimeUnit.MINUTES.toMillis(waitTime);
        }
    }

    @Override
    public void destroy() {
        TransferUtils.finallyClose(stream);
        LOGGER.info("destroy reader with read {} num {}", textFileMetric.tagName.getName(), textFileMetric.readNum.snapshot());
    }
}
