package com.zmops.iot.core.util;


import com.zmops.iot.util.ToolUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Rsa加解密工具
 *
 * @author yefei
 */
public class RsaUtil {

    //公钥为:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwgmYk5tASrKzbjPwYffD9eAt1WRjBL2oN8x0FSff0YBrwdaozUFX0uU6z/gkfcjHwdvEJ7PJAt3ELNZ2xyOcUyeLbS0ds7Yx4pMT7kOYGod4IQDRqjXx0NZIe+f8cijYp9Myg37gL1ptXKV7mWX5cOUfP0g9qI0XLqf3dPSJ77wIDAQAB

    private static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALCCZiTm0BKsrNuM/Bh98P14C3VZGMEvag3zHQVJ9/RgGvB1qjNQVfS5TrP" +
            "+CR9yMfB28Qns8kC3cQs1nbHI5xTJ4ttLR2ztjHikxPuQ5gah3ghANGqNfHQ1kh75/xyKNin0zKDfuAvWm1cpXuZZflw5R8/SD2ojRcup/d09InvvAgMBAAECgYEAmxWXhai" +
            "+/Ql2v1i6TxKo4SvOdYaY93QfbK5Eu1boY3Dleo0HxBo7y8/dVMxJW7XMJwl6c0wa9b23ty+epIxuayDWFiN3xSmQH0l/7H8MMxPY3AA/6U9AoyTUeSoUGRx6W+Lvv12jiVVGiL" +
            "ZNaI6Ucm8irRunPSNdc2YcP5kptgECQQDmJel83TiOICj2TSiC5paAQTouhD31BHOgC5tMetXfmt/im1DGRPmocLqXO2uythZcvrhFGohgk7V6IQWAQ2lvAkEAxFYQSfcJcl2S8JTET" +
            "td15JSUsfO1WCD+3Ik3q1v7jAxc8TubrCPlMZutmDzcalme150Nfh+siEZXyqjGQB/VgQJAGeYkS0/dwGiwE1+Fco2eXBp7AKcTDdKNbHJB7K5aCQq1rRuLUHSMn77n0IL4UKkbBR7VP7" +
            "mHpU6Z+n9izhdH8wJAZMRXL98edhgYpml0J5Ll6x9nAb43QLgOllnGvKhE/qgJu7YAO+VgaImiGxSBfAPrRnZQyn6vKnIP9O+ScK7mAQJAd1zJBY/1ezjD9GvgC6mdG3b1xoBlBVvTxK5T" +
            "0v5jybWe8W/H6gLEprWhzM0LgwA9CsLWe1KXmOcAGeyKwb02CA==";

    /**
     * 私钥解密
     */
    public static String decryptPwd(String password) throws Exception {
        if (ToolUtil.isEmpty(password)) {
            throw new IllegalArgumentException("密码为空！");
        } else {
            return decrypt(password, privateKey);
        }
    }

    /**
     * RSA私钥解密
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        //base64编码的私钥
        byte[]        decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey  = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

}
