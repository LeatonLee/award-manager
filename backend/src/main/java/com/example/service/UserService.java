package com.example.service;

import com.example.pojo.User;
import com.example.pojo.UserDTO;

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
     * @param userDTO
     */
    void registerUser(UserDTO userDTO);

    /**
     * 判断用户是否存在
     * @param name
     * @param phone
     * @return
     */
    boolean isUserExists(String name, String phone);

    User findUserByNameOrPhone(String name, String phone);
}
