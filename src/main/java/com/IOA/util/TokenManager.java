package com.IOA.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.IOA.model.UserModel;


public class TokenManager {

    private static final Key Key = MacProvider.generateKey();
    private static final long Validity = 24 * 60 * 60 * 1000; // 1天

    public static final int Success = 0;
    public static final int ExpiredJwtException = 1;
    public static final int SignatureException = 2;

    public static String generateToken(UserModel user) {
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("id", user.getId());
        userInfo.put("userType", user.getUserType());
        return Jwts.builder().setClaims(userInfo).signWith(SignatureAlgorithm.HS512, TokenManager.Key).setExpiration(new Date(System.currentTimeMillis() + TokenManager.Validity)).compact();
    }

    public static Map<String, Object> parseToken(String token) {
        Map<String, Object> userInfo = new HashMap<String, Object>();
        try {
            Claims claims = Jwts.parser().setSigningKey(TokenManager.Key).parseClaimsJws(token).getBody();
            userInfo.put("id", Integer.parseInt(claims.get("id").toString()));
            userInfo.put("userType", claims.get("userType").toString());
            userInfo.put("error", TokenManager.Success);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            userInfo.put("error", TokenManager.ExpiredJwtException);
        } catch (io.jsonwebtoken.SignatureException e) {
            userInfo.put("error", TokenManager.SignatureException);
        }
        return userInfo;
    }
}
