package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String gradeClass;
    private String phone;
    private String password;
    private String role;//1，学生；2，管理员
    private LocalDate createdAt;
    private LocalDate updatedAt;

}
