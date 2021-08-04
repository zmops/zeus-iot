package com.zmops.iot.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yefei
 **/
public class Test {


    public static void main(String[] args) throws Exception {
        long sec = System.currentTimeMillis();
        System.out.println(sec);

        System.out.println(LocalDateTimeUtils.getLDTByMilliSeconds(sec));
    }



}
