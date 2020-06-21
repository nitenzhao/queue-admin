/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Function: API结果封装.
 *
 * @author zhaoyicheng
 * 2019年11月19日 12:51
 */
@Setter
@Getter
public class Result {

    private int code;

    private String message;

    private Object data;

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
