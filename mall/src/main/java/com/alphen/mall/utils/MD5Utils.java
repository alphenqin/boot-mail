package com.alphen.mall.utils;


import com.alphen.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
//        获取类
        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        进行64位转码
        return Base64.encodeBase64String(md5.digest((strValue+ Constant.SALT).getBytes()));
    }
}
