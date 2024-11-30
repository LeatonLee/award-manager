package com.example.service;

import com.example.pojo.GradeClass;
import com.example.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ClassService {

    // 获取班级列表
    List<GradeClass> getClassList();

    void updateMember(User user);

    void deleteMember(Long id);

    boolean isAdminOfClass(@Param("studentId") Long studentId, @Param("className") String className);

    List<User> findMembersByClassName(String className);

    void addMember(User user);
}
