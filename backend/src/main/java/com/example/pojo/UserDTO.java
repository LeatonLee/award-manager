package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String gradeClass;
    private String phone;
    private String role;//1，学生；2，管理员
    private String password;
    private String confirmPassword;
    private String smsCode;
}
