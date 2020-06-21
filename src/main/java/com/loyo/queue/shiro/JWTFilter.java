/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.shiro;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Function: JWT拦截器.
 *
 * @author zhaoyicheng
 * 2019年11月20日 10:15
 */
@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {

    /**
     * 判断用户是否想要登录
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return !StringUtils.isEmpty(httpRequest.getHeader(AUTHORIZATION_HEADER));
    }

    /**
     * 执行shiro登录操作
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        try {
            HttpServletRequest httpRequest = WebUtils.toHttp(request);
            String token = httpRequest.getHeader(AUTHORIZATION_HEADER);
            JWTUtil.verifyToken(token);
            JWTToken jwtToken = new JWTToken(token);
            getSubject(request, response).login(jwtToken);
            return true;
        } catch (TokenExpiredException tee) {
            request.setAttribute(ShiroConstants.SHIRO_ERROR_ATTR, ShiroConstants.SHIRO_ERROR.EXPIRED_TOKEN.getCode());
            return true;
        } catch (SignatureVerificationException sve) {
            log.info("令牌验证失败", sve);
            request.setAttribute(ShiroConstants.SHIRO_ERROR_ATTR, ShiroConstants.SHIRO_ERROR.INVALID_TOKEN.getCode());
            return false;
        } catch (IncorrectCredentialsException ice) {
            log.info("用户鉴权失败", ice);
            request.setAttribute(ShiroConstants.SHIRO_ERROR_ATTR, ShiroConstants.SHIRO_ERROR.INVALID_TOKEN.getCode());
            return true;
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);
            } catch (Exception e) {
                log.info("用户token鉴权失败:" + ((HttpServletRequest)request).getRequestURI(), e);
            }
        }

        return true;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //支持CORS
        httpResponse.setHeader("Access-control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));

        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (RequestMethod.OPTIONS.name().equals(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }
}
