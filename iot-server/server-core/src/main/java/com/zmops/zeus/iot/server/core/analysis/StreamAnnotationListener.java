package com.zmops.zeus.iot.server.core.analysis;

import com.zmops.zeus.iot.server.core.UnexpectedException;
import com.zmops.zeus.iot.server.core.analysis.worker.RecordStreamProcessor;
import com.zmops.zeus.iot.server.core.annotation.AnnotationListener;
import com.zmops.zeus.iot.server.core.storage.StorageException;
import com.zmops.zeus.iot.server.library.module.ModuleDefineHolder;

import java.lang.annotation.Annotation;

/**
 * @author nantian created at 2021/9/6 17:37
 */
public class StreamAnnotationListener implements AnnotationListener {

    private final ModuleDefineHolder moduleDefineHolder;

    public StreamAnnotationListener(ModuleDefineHolder moduleDefineHolder) {
        this.moduleDefineHolder = moduleDefineHolder;
    }

    @Override
    public Class<? extends Annotation> annotation() {
        return Stream.class;
    }

    @Override
    public void notify(Class aClass) throws StorageException {
        if (aClass.isAnnotationPresent(Stream.class)) {
            Stream stream = (Stream) aClass.getAnnotation(Stream.class);

            if (stream.processor().equals(RecordStreamProcessor.class)) {
                RecordStreamProcessor.getInstance().create(moduleDefineHolder, stream, aClass);
            } else {
                throw new UnexpectedException("Unknown stream processor.");
            }
        } else {
            throw new UnexpectedException("Stream annotation listener could only parse the class present stream annotation.");
        }
    }
}
