/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.common;

/**
 * Function: 结果工厂类.
 *
 * @author zhaoyicheng
 * 2019年11月19日 12:55
 */
public class ResultFactory {

    public static Result successResult(Object data) {
        return buildResult(200, "success", data);
    }

    public static Result failResult(String message) {
        return buildResult(400, message, null);
    }

    public static Result failResult(int code, String message) {
        return buildResult(code, message, null);
    }

    private static Result buildResult(int code, String message, Object data) {
        return new Result(code, message, data);
    }
}
