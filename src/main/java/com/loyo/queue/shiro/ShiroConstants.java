/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.shiro;

/**
 * Function: 系统使用的固定值.
 *
 * @author zhaoyicheng
 * 2019年11月29日 09:44
 */
public class ShiroConstants {
    public static final String SHIRO_ERROR_ATTR = "shiro_error_code";
    public enum SHIRO_ERROR {
        /// <summary>
        /// token不正确
        /// </summary>
        INVALID_TOKEN(50008),
        /// <summary>
        /// token过期
        /// </summary>
        EXPIRED_TOKEN(50014);

        private int code;

        SHIRO_ERROR(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
