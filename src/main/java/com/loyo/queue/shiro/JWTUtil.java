/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Function: JWT Util.
 *
 * @author zhaoyicheng
 * 2019年11月19日 09:29
 */
@Slf4j
public class JWTUtil {
    private static final String JWT_SECRET = "eyJ0eX-AiOiJK-V1QiLC-JhbGci-OiJIUz-UxMiJ9";
    private static final long EXPIRE_TIME = 6 * 60 * 60 * 1000;

    /**
     * 验证token是否有效
     *
     * @param token  令牌
     */
    public static void verifyToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.decode(token);
        String username  = jwt.getClaim("username").asString();
        Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
        JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).build();
        verifier.verify(token);
    }

    private static String getClaim(String token, String claim) throws JWTDecodeException {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim(claim).asString();
    }

    public static String getUsername(String token) {
        return getClaim(token, "username");
    }

    public static Set<String> getRoles(String token) {
        String claim = getClaim(token, "roles");
        if (StringUtils.isEmpty(claim)) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(claim.split(",")));
    }

    public static String sign(String username) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
        return JWT.create().withClaim("username", username).withExpiresAt(date).sign(algorithm);
    }
}
