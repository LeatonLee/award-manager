package com.example.controller;

import com.example.pojo.UserDTO;
import com.example.service.SmsService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    // RSA 私钥（需替换为你实际的私钥）
    private static final String PRIVATE_KEY = "YOUR_PRIVATE_KEY_HERE";

    // 用户注册接口
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        try {
            // 校验是否已注册
            if (userService.isUserExists(userDTO.getName(), userDTO.getPhone())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("学号或手机号已经注册！");
            }

            // 校验验证码
            String storedCode = smsService.getSmsCode(userDTO.getPhone());
            if (storedCode == null || !storedCode.equals(userDTO.getSmsCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("短信验证码错误！");
            }

            // 解密密码
            String decryptedPassword = decryptPassword(userDTO.getPassword());
            String decryptedConfirmPassword = decryptPassword(userDTO.getConfirmPassword());

            // 校验密码一致性
            if (!decryptedPassword.equals(decryptedConfirmPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("两次密码输入不一致！");
            }

            // 将解密后的密码设置回 userDTO
            userDTO.setPassword(decryptedPassword);

            // 注册用户
            userService.registerUser(userDTO);
            return ResponseEntity.ok("注册成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册失败，请稍后重试");
        }
    }

    // 解密密码方法
    private String decryptPassword(String encryptedPassword) throws Exception {
        // 获取私钥对象
        byte[] keyBytes = Base64.getDecoder().decode(PRIVATE_KEY);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // 使用私钥解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));

        return new String(decryptedBytes);
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
