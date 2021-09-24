package com.zmops.zeus.iot.server.transfer.core.validator;


import com.zmops.zeus.iot.server.transfer.api.Validator;

public class PatternValidator implements Validator {

    public PatternValidator(String pattern) {
        this.pattern = pattern;
    }

    private String pattern;

    @Override
    public boolean validate(String messageLine) {
        return messageLine.contains(pattern);
    }
}
