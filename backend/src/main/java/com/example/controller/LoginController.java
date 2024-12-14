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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
        User user = userService.findById(loginRequest.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 直接使用 matches() 方法验证密码，而不是手动加密
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        log.info("BCryptPasswordEncoder.matches() 比较结果: {}", passwordMatches);

        if (!passwordMatches) {
            return Result.error("用户名或密码错误");
        }

        // 生成 JWT 并返回
        Map<String, Object> claims = new HashMap<>();
        claims.put("studentId", user.getId());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());
        claims.put("className", user.getGradeClass());

        String jwt = JwtUtils.generateJwt(claims);
        return Result.success(jwt);
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

        // 禁止浏览器缓存验证码图片
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 生成验证码图片
        BufferedImage image = defaultKaptcha.createImage(text);

        // 设置响应头
        response.setContentType("image/jpeg");

        // 输出图片到客户端
        ImageIO.write(image, "jpg", response.getOutputStream());
    }
}
