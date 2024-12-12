package com.example.controller;

import com.example.pojo.Result;
import com.example.pojo.User;
import com.example.pojo.UserLoginRequest;
import com.example.service.UserService;
import com.example.utils.JwtUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:7778", allowCredentials = "true")
@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private DefaultKaptcha defaultKaptcha; // 用于验证码生成

    @PostMapping("/login")
    public Result login(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {
        log.info("接收到的登录请求：id={}, password={}, verificationCode={}", loginRequest.getId(), loginRequest.getPassword(), loginRequest.getVerificationCode());

        // 获取 Session 中的验证码
        String sessionCaptcha = (String) request.getSession().getAttribute("captcha");
        log.info("Session 中的验证码: {}", sessionCaptcha);

        // 验证验证码
        if (sessionCaptcha == null || !sessionCaptcha.equals(loginRequest.getVerificationCode())) {
            return Result.error("验证码错误");
        }

        // 根据学号查询用户
        User u = userService.login(loginRequest); // 假设 userService 提供 findById 方法

        if (u == null) {
            return Result.error("用户不存在");
        }

        // 验证密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginRequest.getPassword(), u.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 密码正确，生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("studentId", u.getId());
        claims.put("name", u.getName());
        claims.put("role", u.getRole());
        claims.put("className", u.getGradeClass());

        String jwt = JwtUtils.generateJwt(claims); // 生成 JWT
        return Result.success(jwt); // 返回 JWT
    }




    // 获取验证码
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 打印当前 session ID
        System.out.println("当前 session ID: " + request.getSession().getId());

        // 生成验证码文本
        String text = defaultKaptcha.createText();
        log.info("生成的验证码: {}", text);

        // 将验证码保存到 Session
        request.getSession().setAttribute("captcha", text);
        log.info("验证码已保存到 Session: {}", text); // 输出存入 Session 的验证码

        // 生成验证码图片
        BufferedImage image = defaultKaptcha.createImage(text);

        // 设置响应头
        response.setContentType("image/jpeg");

        // 输出图片到客户端
        ImageIO.write(image, "jpg", response.getOutputStream());
    }


}
