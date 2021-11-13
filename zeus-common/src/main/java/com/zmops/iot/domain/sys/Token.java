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
@Table(name = "token")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    private Integer tokenId;

    private String name;

    private String description;

    private Long userId;

    private String token;

    private String status;

    private Long expiresAt;

    private String account;
}
