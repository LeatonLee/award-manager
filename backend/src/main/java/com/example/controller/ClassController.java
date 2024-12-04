package com.example.controller;

import com.example.pojo.PageBean;
import com.example.pojo.Result;
import com.example.service.ClassService;
import com.example.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/{className}")
    public Result getClassMembers(@PathVariable String className,
                                  @RequestHeader("Authorization") String token,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
            // 从 Bearer Token 中提取 JWT 部分
            String jwt = token.replace("Bearer ", "");
            Claims claims = JwtUtils.parseJWT(jwt); // 解析 JWT
            Long studentId = Long.parseLong(claims.get("studentId").toString()); // 提取 studentId

            PageBean pageBean = classService.page(page,pageSize,className);
            return Result.success(pageBean);
    }
}
