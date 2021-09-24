package com.zmops.zeus.iot.server.transfer.core.validator;

import com.zmops.zeus.iot.server.transfer.core.api.Validator;

public class PrefixValidator implements Validator {

    public PrefixValidator(String prefix) {
        this.prefix = prefix;
    }

    private String prefix;

    @Override
    public boolean validate(String messageLine) {
        return messageLine.startsWith(prefix);
    }
}
