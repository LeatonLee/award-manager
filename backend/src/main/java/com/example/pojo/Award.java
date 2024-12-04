package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Award {
    private Long id; // 奖项ID
    private Long userId; // 关联的用户ID
    private String awardName; // 奖项名称
    private String awardLevel; // 奖项级别
    private Date awardDate; // 获奖日期
    private String awardDescription; // 奖项描述
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间

}
