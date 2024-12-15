package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;
    private String name;
    private String gradeClass;
    private String phone;
    private String password;
    private String role;//1，学生；2，管理员
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int awardCount; // 添加获奖数量字段
}
