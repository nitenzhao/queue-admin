/*
 * Copyright (c) 2015-2020.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.util;

import org.springframework.util.DigestUtils;

/**
 * Function: 字符串哈希工具类.
 *
 * @author zhaoyicheng
 * 2020年01月11日 14:41
 */
public class HashUtil {

    /**
     * md5哈希
     * @param text  需要哈希的字符串
     * @return      哈希后的字符串
     */
    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    /**
     * 带盐的md5哈希
     * @param text      需要哈希的字符串
     * @param salt      盐值
     * @return          哈希后的字符串
     */
    public static String md5WithSalt(String text, String salt) {
        return DigestUtils.md5DigestAsHex((text + salt).getBytes());
    }
}
