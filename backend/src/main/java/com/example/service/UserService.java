package com.example.service;

import com.example.pojo.User;
import com.example.pojo.UserDTO;

/**
 * 用户管理
 */
public interface UserService {

    /**
     * 注册用户
     * @param userDTO
     */
    void registerUser(UserDTO userDTO);

    /**
     * 根据id查找用户
     * @param id
     * @return
     */
    User findById(Long id);

}
