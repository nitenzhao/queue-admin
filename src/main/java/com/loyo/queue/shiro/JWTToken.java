/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Function: JWT令牌.
 *
 * @author zhaoyicheng
 * 2019年11月20日 10:21
 */
public class JWTToken implements AuthenticationToken {
    private static final long serialVersionUID = -3325412440701703296L;

    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
