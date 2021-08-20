package com.zmops.iot.core.util;

import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.exception.enums.CoreExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author nantian created at 2021/7/30 15:32
 */
public class FileUtil {

    private static Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static byte[] toByteArray(String filename) {

        File f = new File(filename);
        if (!f.exists()) {
            log.error("文件未找到！" + filename);
            throw new ServiceException(CoreExceptionEnum.FILE_NOT_FOUND);
        }
        FileChannel     channel = null;
        FileInputStream fs      = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            throw new ServiceException(CoreExceptionEnum.FILE_READING_ERROR);
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                throw new ServiceException(CoreExceptionEnum.FILE_READING_ERROR);
            }
            try {
                fs.close();
            } catch (IOException e) {
                throw new ServiceException(CoreExceptionEnum.FILE_READING_ERROR);
            }
        }
    }
}
