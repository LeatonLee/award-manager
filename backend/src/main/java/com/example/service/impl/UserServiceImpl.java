package com.example.service.impl;

import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.pojo.UserDTO;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 注册用户
     * @param userDTO
     */
    @Override
    public void registerUser(UserDTO userDTO) {
        if (userDTO.getId() == null || !userDTO.getId().matches("\\d+")) {
            throw new IllegalArgumentException("无效的学号！");
        }
        // 将 UserDTO 转换为 User 实体
        User user = new User();
        user.setId(Long.valueOf(userDTO.getId()));
        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setGradeClass(userDTO.getGradeClass());
        user.setRole(userDTO.getRole());
        user.setPassword(userDTO.getPassword());  // 加密密码
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());

        // 保存用户到数据库
        userMapper.saveUser(user);
    }

}
