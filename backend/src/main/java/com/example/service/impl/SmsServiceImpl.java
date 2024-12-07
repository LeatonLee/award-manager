package com.example.service.impl;

import com.example.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 发送短信验证码并保存到 Redis
    public boolean sendSms(String phone) {
        String code = generateCode();  // 生成验证码
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);  // 将验证码存入 Redis，5分钟有效期
        // 调用第三方短信服务发送验证码（此处为模拟）
        // 实际可以调用短信平台的API
        System.out.println("短信验证码：" + code);
        return true;  // 返回成功
    }

    // 获取存储在 Redis 中的验证码
    public String getSmsCode(String phone) {
        return redisTemplate.opsForValue().get(phone);
    }

    // 生成验证码
    private String generateCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000); // 生成6位验证码
    }
}
