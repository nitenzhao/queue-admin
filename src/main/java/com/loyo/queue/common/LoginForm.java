/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Function: 登录表单.
 *
 * @author zhaoyicheng
 * 2019年11月27日 16:17
 */
@Getter
@Setter
@ApiModel(value = "登录表单")
@Validated
public class LoginForm {
    @ApiModelProperty(value = "用户登录名", position = 1)
    @NotEmpty
    private String username;

    @ApiModelProperty(value = "用户密码", position = 2)
    @NotEmpty
    private String password;
}
