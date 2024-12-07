package com.example.controller;

import com.example.pojo.Result;
import com.example.pojo.User;
import com.example.service.UserService;
import com.example.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:7778", allowCredentials = "true")
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     *
     * @param user 包含用户名和密码的User对象
     * @return 登录结果，成功返回JWT，失败返回错误信息
     */
    @PostMapping("/api/login")
    public Result login(@RequestBody User user) {
        log.info("用户登陆：{}", user);

        // 根据id和password查询用户
        User u = userService.login(user);

        if (u != null) {
            // 验证密码是否正确
            if (user.getPassword().equals(u.getPassword())){
                // 密码正确，生成JWT
                Map<String, Object> claims = new HashMap<>();
                claims.put("studentId", u.getId());
                claims.put("name", u.getName());
                claims.put("role", u.getRole());
                claims.put("className", u.getGradeClass());

                String jwt = JwtUtils.generateJwt(claims); // 生成JWT
                return Result.success(jwt); // 包含"status" 和 "data"
            } else {
                // 密码错误
                return Result.error("用户名或密码错误");
            }
        }
        // 用户不存在
        return Result.error("用户名或密码错误");
    }
}
