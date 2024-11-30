package com.example.controller;

import com.example.pojo.GradeClass;
import com.example.pojo.User;
import com.example.service.ClassService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.utils.JwtUtils;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    // 获取班级列表
    @GetMapping
    public List<GradeClass> getClassList() {
        return classService.getClassList();
    }

    // 获取班级成员列表
    @GetMapping("/{className}")
    public ResponseEntity<?> getClassMembers(@PathVariable String className, @RequestHeader("Authorization") String token) {
        try {
            // 从 Bearer Token 中提取 JWT 部分
            String jwt = token.replace("Bearer ", "");
            Claims claims = JwtUtils.parseJWT(jwt); // 解析 JWT
            Long studentId = Long.parseLong(claims.get("studentId").toString()); // 提取 adminId

            // 检查是否是该班级的管理员
            boolean isAdmin = classService.isAdminOfClass(studentId, className);
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限访问该班级的成员列表");
            }

            // 查找班级成员
            List<User> members = classService.findMembersByClassName(className);
            return ResponseEntity.ok(members);

        } catch (Exception e) {
            // 打印异常堆栈
            e.printStackTrace(); // 打印详细异常堆栈
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取班级成员列表时发生错误：" + e.getMessage());
        }
    }

    // 添加班级成员
    @PostMapping("/{className}/members")
    public ResponseEntity<?> addMember(
            @PathVariable String className,
            @RequestBody User user,
            @RequestParam Long adminId) {
        try {
            if (classService.isAdminOfClass(adminId, className)) {
                classService.addMember(user);
                return ResponseEntity.status(HttpStatus.CREATED).body("成员添加成功");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限添加成员");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("添加成员时发生错误：" + e.getMessage());
        }
    }

    // 更新班级成员信息
    @PutMapping("/members/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            classService.updateMember(user);
            return ResponseEntity.ok("成员更新成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("更新成员时发生错误：" + e.getMessage());
        }
    }

    // 删除班级成员
    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            classService.deleteMember(id);
            return ResponseEntity.ok("成员删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除成员时发生错误：" + e.getMessage());
        }
    }
}
