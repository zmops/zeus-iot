package com.zmops.zeus.iot.server.transfer.core.job;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(version = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandEntity {

    @PrimaryKey
    private String id;

    private int commandResult;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private boolean isAcked;

    private String taskId;

    private String deliveryTime;

    public static String generateCommanid(String taskId, int opType) {
        return taskId + opType;
    }
}
