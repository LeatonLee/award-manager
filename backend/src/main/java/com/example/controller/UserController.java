package com.example.controller;

import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 用户注册接口
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // 验证用户是否已经存在
        if (userService.isUserExists(user.getName(), user.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("学号或手机号已经注册！");
        }
        try {
            // 设置默认角色
            user.setRole("1");
            // 保存用户
            userService.registerUser(user);
            return ResponseEntity.ok("注册成功！");
        } catch (Exception e) {
            e.printStackTrace();  // 打印错误栈信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册失败，请稍后重试");
        }
    }


}
