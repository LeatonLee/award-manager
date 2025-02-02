package com.example.controller;

import com.example.pojo.GradeClass;
import com.example.pojo.PageBean;
import com.example.pojo.Result;
import com.example.pojo.UpdateGradeClassRequest;
import com.example.service.ClassService;
import com.example.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 获取班级列表
     * @return
     */
    @GetMapping
    public List<GradeClass> getClassList() {
        return classService.getClassList();
    }

    @GetMapping("/{className}")
    public Result getClassMembers(@PathVariable String className,
                                  @RequestHeader("Authorization") String token,
                                  @RequestParam(required = false) String name,  // 姓名模糊搜索
                                  @RequestParam(required = false) Long id,    // 学号模糊搜索
                                  @RequestParam(defaultValue = "0") Integer sortByAwards,  // 按获奖数量排序 0: 升序, 1: 降序
                                  @RequestParam(defaultValue = "1") Integer page, // 当前页码
                                  @RequestParam(defaultValue = "10") Integer pageSize) { // 每页数据量
        // 从 Bearer Token 中提取 JWT 部分
        String jwt = token.replace("Bearer ", "");
        Claims claims = JwtUtils.parseJWT(jwt); // 解析 JWT
        Long studentId = Long.parseLong(claims.get("studentId").toString()); // 提取 studentId

        // 调用 service 层的方法，传入分页和搜索条件
        PageBean pageBean = classService.page(page, pageSize, className, name, id, sortByAwards);

        // 返回分页结果
        return Result.success(pageBean);
    }

    /**
     * 更新用户的获奖数量
     * @param userId
     */
    @PostMapping("/updateAwardCount")
    public void updateAwardCount(@RequestParam Long userId) {
        classService.updateAwardCount(userId);
    }

    @PostMapping("/updateGradeClass")
    public Result updateGradeClass(@RequestBody UpdateGradeClassRequest request) {
        classService.updateGradeClass(request.getUserId(), request.getGradeClass());
        return Result.success();
    }
}
