package com.example.service;

import com.example.pojo.User;
import com.example.pojo.UserDTO;
import com.example.pojo.UserLoginRequest;

/**
 * 用户管理
 */
public interface UserService {

    /**
     * 注册用户
     * @param userDTO
     */
    void registerUser(UserDTO userDTO);

    User login(UserLoginRequest user);

    User findUserByNameOrPhone(String name, String phone);

    User findById(Long id);

}
