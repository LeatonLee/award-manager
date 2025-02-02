package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwardRequest {
    private String awardName;         // 奖项名称
    private String awardLevel;        // 奖项级别
    private LocalDate awardDate;      // 获奖日期，使用 LocalDate 类型
    private String certificateUrl;    // 证书 URL（如果需要上传文件的 URL）
}


