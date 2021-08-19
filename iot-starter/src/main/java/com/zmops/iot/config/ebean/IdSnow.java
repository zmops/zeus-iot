package com.zmops.iot.config.ebean;

import cn.hutool.core.util.IdUtil;
import com.zmops.iot.constant.IdTypeConsts;
import io.ebean.config.IdGenerator;

/**
 * @author nantian created at 2021/8/1 18:57
 */
public class IdSnow implements IdGenerator {

    @Override
    public Object nextValue() {
        return IdUtil.getSnowflake().nextId();
    }

    @Override
    public String getName() {
        return IdTypeConsts.ID_SNOW;
    }
}
