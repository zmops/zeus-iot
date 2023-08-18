package com.zmops.iot.domain.messages;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 */

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "messages")
public class Messages {
    @Id
    private Integer id;
    private Integer classify;
    private String title;
    private String content;
    private Long clock;
    private String module;
    private Integer readed;
    private Long userId;
}
