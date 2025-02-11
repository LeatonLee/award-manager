package com.example.controller;

import com.example.pojo.AwardRequest;
import com.example.pojo.PageBean;
import com.example.pojo.Result;
import com.example.service.AwardService;
import com.example.service.ClassService;
import com.example.utils.JwtUtils;
import com.example.utils.OssUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/award")
public class AwardController {

    @Autowired
    private OssUtils ossUtils;

    @Autowired
    private ClassService classService;

    @Autowired
    private AwardService awardService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 证书上传
     * @param file
     * @param authorizationHeader
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 解析 JWT token，验证用户是否合法
        Claims claims = jwtUtils.parseJWT(token);
        if (claims == null) {
            return ResponseEntity.status(401).body("无效的 token");
        }

        Long userId = Long.parseLong(claims.get("studentId").toString());  // 提取 studentId
        if (userId == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }
        try {
            // 上传文件到 OSS
            String fileUrl = ossUtils.uploadFile(file.getInputStream(), file.getOriginalFilename());
            return ResponseEntity.ok(fileUrl); // 返回文件 URL
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("文件上传失败");
        }
    }

    /**
     * 添加奖项信息
     *
     * @param awardRequest        奖项信息
     * @param authorizationHeader 用户请求信息
     * @return 是否成功
     */
    @PostMapping("/add")
    public ResponseEntity<String> addAward(
            @RequestBody AwardRequest awardRequest,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 解析 JWT token，验证用户是否合法
        Claims claims = jwtUtils.parseJWT(token);
        if (claims == null) {
            return ResponseEntity.status(401).body("无效的 token");
        }

        Long userId = Long.parseLong(claims.get("studentId").toString());  // 提取 studentId
        if (userId == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }

        boolean success = awardService.addAward(awardRequest, userId);
        if (success) {
            // 新增：更新用户的获奖总数
            classService.updateAwardCount(userId);
            return ResponseEntity.ok("奖项添加成功");
        } else {
            return ResponseEntity.status(500).body("奖项添加失败");
        }
    }

    /**
     * 获取获奖信息
     * @param page
     * @param pageSize
     * @param className
     * @return
     */
    @GetMapping("/list")
    public Result getAwardList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam String className
    ) {
        PageBean pageBean = awardService.getAwardList(page, pageSize, className);
        return Result.success(pageBean);
    }

    /**
     * 删除获奖信息
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteAward(@PathVariable Long id) {
        awardService.deleteAward(id);
        return Result.success();
    }

    /**
     * 用户历史获奖
     *
     * @param page
     * @param pageSize
     * @param keyword
     * @param authorizationHeader
     * @return
     */
    @GetMapping("/user/list")
    public Result getUserAwards(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // 解析 Token
        String token = authorizationHeader.replace("Bearer ", "");
        Claims claims = jwtUtils.parseJWT(token);
        if (claims == null) {
            return Result.error("无效的 token");
        }

        Long userId = Long.parseLong(claims.get("studentId").toString());
        PageBean pageBean = awardService.getUserAwards(page, pageSize, userId, keyword);
        return Result.success(pageBean);
    }

    /**
     * 用户编辑获奖信息
     * @param id
     * @param awardName
     * @param awardLevel
     * @param awardDate
     * @param certificateFile
     * @param authorizationHeader
     * @return
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAward(
            @PathVariable Long id,
            @RequestParam("awardName") String awardName,
            @RequestParam("awardLevel") String awardLevel,
            @RequestParam("awardDate") String awardDate,
            @RequestPart(value = "certificate", required = false) MultipartFile certificateFile,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // 1. 校验 Token 和用户权限
        String token = authorizationHeader.replace("Bearer ", "");
        Claims claims = jwtUtils.parseJWT(token);
        if (claims == null) {
            return ResponseEntity.status(401).body("无效的 token");
        }
        Long userId = Long.parseLong(claims.get("studentId").toString());

        // 2. 处理证书文件上传
        String certificateUrl = null;
        if (certificateFile != null && !certificateFile.isEmpty()) {
            try {
                certificateUrl = ossUtils.uploadFile(certificateFile.getInputStream(), certificateFile.getOriginalFilename());
            } catch (IOException e) {
                return ResponseEntity.status(500).body("证书上传失败");
            }
        }

        // 3. 构建更新对象
        AwardRequest awardRequest = new AwardRequest();
        awardRequest.setAwardName(awardName);
        awardRequest.setAwardLevel(awardLevel);
        awardRequest.setAwardDate(LocalDate.parse(awardDate));
        awardRequest.setCertificateUrl(certificateUrl); // 如果未上传新文件，certificateUrl 可能为 null

        // 4. 调用 Service 更新数据
        boolean success = awardService.updateAward(id, awardRequest);
        return success ?
                ResponseEntity.ok("更新成功") :
                ResponseEntity.status(500).body("更新失败");
    }
}
