package com.example.service;

import com.example.pojo.User;

/**
 * 用户管理
 */
public interface UserService {
    /**
     * 用户登录
     * @param user
     * @return
     */
    User login(User user);

    /**
     * 注册用户
     * @param user
     */
    void registerUser(User user);

    /**
     * 判断用户是否存在
     * @param name
     * @param phone
     * @return
     */
    boolean isUserExists(String name, String phone);

    User findUserByNameOrPhone(String name, String phone);
}
