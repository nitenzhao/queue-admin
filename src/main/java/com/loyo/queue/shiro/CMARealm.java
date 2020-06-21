/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.shiro;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.loyo.queue.user.User;
import com.loyo.queue.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Function: JWT Realm.
 *
 * @author zhaoyicheng
 * 2019年11月10日 16:29
 */
@Log4j2
@Service
public class CMARealm extends AuthorizingRealm {

    private UserService userService;

    @Autowired
    void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     *  是否支持令牌
     * @param token 认证令牌
     * @return      是否
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 获取认证主体对应的权限
     * @param principalCollection       认证主体
     * @return                          权限信息
     * @throws AuthenticationException  认证异常
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) throws AuthenticationException {
        if (principalCollection == null) {
            throw new AuthenticationException("No principals");
        }

        String token = (String) getAvailablePrincipal(principalCollection);
        String username = JWTUtil.getUsername(token);
        User user = userService.findByUsername(username);
        return new SimpleAuthorizationInfo();
    }

    /**
     * 对认证令牌进行验证
     * @param authToken     认证令牌
     * @return              认证信息
     * @throws AuthenticationException  认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        String token = (String) authToken.getPrincipal();
        try {
            JWTUtil.verifyToken(token);
            String username = JWTUtil.getUsername(token);
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new UnknownAccountException("账户不存在");
            }
        } catch (JWTVerificationException je) {
            log.info("认证令牌验证失败", je);
            throw new IncorrectCredentialsException("认证令牌验证失败");

        }

        return new SimpleAuthenticationInfo(token, token, getName());
    }
}
