package com.example.service;

import com.example.pojo.GradeClass;
import com.example.pojo.PageBean;

import java.util.List;


public interface ClassService {

    /**
     * 获取班级列表
     * @return
     */
    List<GradeClass> getClassList();

    PageBean page(Integer page, Integer pageSize, String className, String name, Long id, Integer sortByAwards);

    /**
     * 更新用户的获奖数量
     * @param userId
     */
    void updateAwardCount(Long userId);

    void updateGradeClass(Long userId, String gradeClass);
}
