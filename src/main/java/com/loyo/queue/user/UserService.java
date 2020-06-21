/*
 * Copyright (c) 2015-2019.   LoyoTech All Rights Reserved.
 */

package com.loyo.queue.user;

import com.loyo.queue.shiro.JWTUtil;
import com.loyo.queue.util.HashUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * Function: 用户服务类.
 *
 * @author zhaoyicheng
 * 2019年12月06日 13:48
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public User getUserInfo(String token) {
        String username = JWTUtil.getUsername(token);
        User user = findByUsername(username);
        user.setPassword("");
        return user;
    }

    public List<User> find(String text) {
        if (!StringUtils.isEmpty(text)) {
            Example example = new Example(User.class);
            example.createCriteria().andLike("username", "%" + text + "%");
            return userMapper.selectByExample(example);
        }
        return userMapper.selectAll();
    }

    public int insert(User user) {
        if (StringUtils.isEmpty(user.getSalt())) {
            user.setSalt(RandomStringUtils.randomAlphanumeric(8));
        }
        return userMapper.insertSelective(user);
    }

    public int update(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public int delete(long id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    public User findByUsername(String username) {
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("username", username);
        return userMapper.selectOneByExample(example);
    }

    public int updatePassword(long userId, String oldPwd, String newPwd) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user.getPassword().equals(HashUtil.md5WithSalt(oldPwd, user.getSalt()))) {
            user.setPassword(HashUtil.md5WithSalt(newPwd, user.getSalt()));
            return userMapper.updateByPrimaryKeySelective(user);
        } else {
            throw new IncorrectCredentialsException("用户名/密码不匹配");
        }
    }

    public int resetPassword(long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        String password = HashUtil.md5WithSalt(HashUtil.md5("password"), user.getSalt());
        user = new User();
        user.setId(userId);
        user.setPassword(password);
        return userMapper.updateByPrimaryKeySelective(user);
    }
}
