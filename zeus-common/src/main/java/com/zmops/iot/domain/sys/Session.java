package com.zmops.iot.domain.sys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@Data
@Table(name = "session")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    private Integer id;

    private String key;

    private String value;

}
