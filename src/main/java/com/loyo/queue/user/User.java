/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.user;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Function: 用户实体类.
 *
 * @author zhaoyicheng
 * 2019年12月05日 17:38
 */

@Getter
@Setter
@Table(name = "system_user")
@ApiModel(value = "系统用户实体")
@Validated
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private String name;
    private String mobile;

    private String salt;
    private Integer status;
    private Integer kind;
    private LocalDateTime createTime;
}
