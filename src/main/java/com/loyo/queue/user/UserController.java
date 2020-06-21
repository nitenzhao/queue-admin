/*
 * Copyright (c) 2015-2020.   Bondeasy All Rights Reserved.
 */

package com.loyo.queue.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loyo.queue.common.LoginForm;
import com.loyo.queue.common.Result;
import com.loyo.queue.common.ResultFactory;
import com.loyo.queue.shiro.JWTToken;
import com.loyo.queue.shiro.JWTUtil;
import com.loyo.queue.util.HashUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Function: 用户控制类.
 *
 * @author zhaoyicheng
 * 2019年12月06日 13:53
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "用户名/密码登录", notes = "用户登录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result login(@ApiParam(name = "loginForm", value = "登录表单") @RequestBody LoginForm loginForm,
                        HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
            String username = loginForm.getUsername();
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new UnknownAccountException("用户名不存在");
            }

            if (user.getStatus() > 0) {
                throw new AccountException("账户已被禁止登录!");
            }

            if (!user.getPassword().equals(HashUtil.md5WithSalt(loginForm.getPassword(), user.getSalt()))) {
                throw new IncorrectCredentialsException(username + "用户名/密码不正确");
            }

            String token = JWTUtil.sign(loginForm.getUsername());
            AuthenticationToken jwtToken = new JWTToken(token);
            try {
                subject.login(jwtToken);
                response.setHeader("Authorization", token);
                return ResultFactory.successResult(token);
            } catch (IncorrectCredentialsException ice) {
                log.error("用户名/密码错误");
            } catch (AuthenticationException ae) {
                log.error("登录失败", ae);
            }
        return ResultFactory.failResult("用户登录失败");
    }

    @ApiOperation(value = "根据token获取用户信息", notes = "用户查询")
    @GetMapping("info")
    @RequiresAuthentication
    public Result getUserInfo(ServletRequest request, @RequestParam(required = false) String token) {
        if (StringUtils.isEmpty(token)) {
            HttpServletRequest httpRequest = WebUtils.toHttp(request);
            token = httpRequest.getHeader("Authorization");
        }
        return ResultFactory.successResult(userService.getUserInfo(token));
    }

    @ApiOperation(value = "查询所有用户", notes = "根据用户名模糊查询所有用户，带有分页功能")
    @GetMapping("")
    @RequiresAuthentication
    public Result findUsers(@RequestParam(name = "name", required = false, defaultValue = "") String name,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        PageHelper.startPage(page, limit);
        List<User> users = userService.find(name);
        return ResultFactory.successResult(new PageInfo<>(users));
    }

    @ApiOperation(value = "往系统里添加一个新用户", notes = "添加用户")
    @PostMapping("")
    @RequiresRoles("admin")
    public Result insertUser(@ApiParam(value = "新用户信息", required = true) @Validated @RequestBody User user) {
        return ResultFactory.successResult(userService.insert(user));
    }

    @ApiOperation(value = "修改用户信息", notes = "修改用户")
    @PutMapping("{userId:\\d+}")
    @RequiresRoles("admin")
    public Result updateUser(@ApiParam(name = "userId", value = "用户id") @PathVariable("userId") long userId,
                             @ApiParam(value = "要修改的用户信息", required = true) @Validated @RequestBody User user) {
        user.setId(userId);
        return ResultFactory.successResult(userService.update(user));
    }

    @ApiOperation(value = "根据用户id删除用户", notes = "删除用户")
    @DeleteMapping("{userId:\\d+}")
    @RequiresRoles("admin")
    public Result deleteUser(@ApiParam(value = "用户id", required = true) @PathVariable("userId") long userId) {
        return ResultFactory.successResult(userService.delete(userId));
    }

    @ApiOperation(value = "重置用户密码", notes = "管理员将用户密码重置为初始密码")
    @PostMapping("{userId:\\d+}/reset")
    @RequiresRoles("admin")
    public Result resetPassword(@ApiParam(name = "userId", value = "用户id", required = true) @PathVariable("userId") long userId) {
        return ResultFactory.successResult(userService.resetPassword(userId));
    }

    @ApiOperation(value = "修改用户密码", notes = "用户将原密码更新为新密码")
    @PostMapping("{userId:\\d+}/change")
    @RequiresAuthentication
    public Result changePassword(@ApiParam(name = "userId", value = "用户id") @PathVariable("userId") long userId,
                                 @ApiParam(name = "oldPwd", value = "原密码") @RequestParam("oldPwd") String oldPwd,
                                 @ApiParam(name = "newPwd", value = "新密码") @RequestParam("newPwd") String newPwd) {
        return ResultFactory.successResult(userService.updatePassword(userId, oldPwd, newPwd));
    }

    @ApiOperation(value = "用户退出登录", notes = "用户登录")
    @PostMapping("logout")
    public Result logout(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String token = httpRequest.getHeader("Authorization");
        return ResultFactory.successResult(token);
    }
}
