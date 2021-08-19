package com.zmops.iot.core.auth.jwt.payload;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * jwt的第二部分
 *
 * @author fengshuonan
 */
@Data
public class JwtPayLoad {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户的键
     */
    private String userKey;

    public JwtPayLoad() {
    }

    public JwtPayLoad(Long userId, String account, String userKey) {
        this.userId = userId;
        this.account = account;
        this.userKey = userKey;
    }

    /**
     * payload转化为map形式
     *
     * @author fengshuonan
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", this.userId);
        map.put("account", this.account);
        map.put("userKey", this.userKey);
        return map;
    }

    /**
     * payload转化为map形式
     *
     * @author fengshuonan
     */
    public static JwtPayLoad toBean(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return new JwtPayLoad();
        } else {
            JwtPayLoad jwtPayLoad = new JwtPayLoad();

            Object userId = map.get("userId");
            if (userId instanceof Long) {
                jwtPayLoad.setUserId(Long.valueOf(map.get("userId").toString()));
            }

            jwtPayLoad.setAccount((String) map.get("account"));
            jwtPayLoad.setUserKey((String) map.get("userKey"));
            return jwtPayLoad;
        }
    }

}
