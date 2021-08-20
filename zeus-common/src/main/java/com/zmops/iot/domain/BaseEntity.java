package com.zmops.iot.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import io.ebean.annotation.WhoCreated;
import io.ebean.annotation.WhoModified;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author nantian created at 2021/7/30 20:37
 */
@MappedSuperclass
public abstract class BaseEntity extends Model {

    @WhenCreated
    LocalDateTime createTime;

    @WhenModified
    LocalDateTime updateTime;

    @WhoCreated
    Long createUser;

    @WhoModified
    Long updateUser;

    /**
     * 数据校验组:新增
     */
    public interface Create {
    }

    /**
     * 数据校验组:更新
     */
    public interface Update {
    }

    /**
     * 数据校验组:删除
     */
    public interface Delete {
    }

    /**
     * 数据校验组:绑定设备
     */
    public interface BindDevice {
    }

    /**
     * 数据校验组:查询
     */
    public interface Get {
    }

    /**
     * 数据校验组:批量删除
     */
    public interface MassRemove {
    }

}
