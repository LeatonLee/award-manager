package com.example.controller;

import com.example.pojo.UserDTO;
import com.example.service.SmsService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 用户注册接口
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        try {
            // 校验两次密码是否一致
            if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("两次密码输入不一致！");
            }

            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(encodedPassword);

            // 注册用户
            userService.registerUser(userDTO);
            return ResponseEntity.ok("注册成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册失败，请稍后重试");
        }
    }


    // 发送短信验证码
    @PostMapping("/send-sms")
    public ResponseEntity<String> sendSmsCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        boolean isSent = smsService.sendSms(phone); // 调用短信服务发送验证码
        if (isSent) {
            return ResponseEntity.ok("验证码已发送");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("验证码发送失败");
        }
    }
}
