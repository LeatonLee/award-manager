package com.example.service.impl;

import com.example.mapper.ClassMapper;
import com.example.pojo.GradeClass;
import com.example.pojo.User;
import com.example.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    public boolean isAdminOfClass(Long studentId, String className) {
        try {
            return classMapper.isAdminOfClass(studentId, className);
             // 如果结果大于 0，表示是管理员
        } catch (Exception e) {
            // 打印异常堆栈
            e.printStackTrace();  // 记录详细异常信息
            throw new RuntimeException("数据库查询失败", e);  // 重新抛出异常，便于调试
        }
    }


    public List<User> findMembersByClassName(String className) {
        return classMapper.findMembersByClassName(className);
    }

    public void addMember(User user) {
        classMapper.insertMember(user);
    }

    @Override
    public List<GradeClass> getClassList() {
        return classMapper.getAllClasses();
    }

    public void updateMember(User user) {
        classMapper.updateMember(user);
    }

    public void deleteMember(Long id) {
        classMapper.deleteMember(id);
    }
}
