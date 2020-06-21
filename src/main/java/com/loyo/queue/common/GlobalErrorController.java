/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.common;

import com.loyo.queue.shiro.ShiroConstants;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Function: 全局管理系统中抛出的异常.
 *
 * @author zhaoyicheng
 * 2019年11月11日 17:32
 */
@Log4j2
@RestControllerAdvice
public class GlobalErrorController {
    /**捕捉shiro的异常
     *
     * @param request   http请求
     * @param se        shiro异常
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Result handle401(HttpServletRequest request, ShiroException se) {
        if (!StringUtils.isEmpty(request.getAttribute(ShiroConstants.SHIRO_ERROR_ATTR))) {
            Integer statusCode = (Integer) request.getAttribute(ShiroConstants.SHIRO_ERROR_ATTR);
            return ResultFactory.failResult(statusCode, "用户token验证失败");
        }
        return ResultFactory.failResult(HttpStatus.UNAUTHORIZED.value(), se.getMessage());
    }

    /**
     *     捕捉UnauthorizedException
      */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Result handle401() {
        return ResultFactory.failResult(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }

    /**
     *     捕捉UnauthenticatedException
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public Result handleUnauthenticated() {
        return ResultFactory.failResult(ShiroConstants.SHIRO_ERROR.EXPIRED_TOKEN.getCode(), "用户认证已失效，请刷新页面重新登录");
    }

    /**
     * 捕获校验时参数绑定异常
     * @param request       http请求
     * @param exception     参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result handle(HttpServletRequest request, BindException exception) {
        //获取参数校验错误集合
        List<FieldError> fieldErrors = exception.getFieldErrors();
        //格式化以提供友好的错误提示
        String data = String.format("参数校验错误（%s）：%s", fieldErrors.size(),
                fieldErrors.stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining(";")));
        //参数校验失败响应失败个数及原因
        return ResultFactory.failResult(getStatus(request).value(), data);
    }

    /**
     * 捕捉数据库操作异常
     * @param de        数据库异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result dataException(DataIntegrityViolationException de) {
        log.warn("数据库操作异常", de);
        return ResultFactory.failResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "违反数据完整性约束,不能执行此操作");
    }

    /**
     * 捕捉其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result globalException(HttpServletRequest request, Throwable ex) {
        log.info("系统出现异常", ex);
        return ResultFactory.failResult(getStatus(request).value(), ex.getMessage());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(ShiroConstants.SHIRO_ERROR_ATTR);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
