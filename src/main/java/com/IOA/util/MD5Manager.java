package com.IOA.util;

import org.apache.commons.codec.digest.DigestUtils;

import com.IOA.model.UserModel;

public class MD5Manager {
    public static String encode(String pwd){
        return DigestUtils.md5Hex(pwd);
    }

    public static boolean verify(String tgt,String src){
        return DigestUtils.md5Hex(tgt).equals(src);
    }
}
