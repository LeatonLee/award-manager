package com.example.service;

import com.example.pojo.GradeClass;
import com.example.pojo.PageBean;

import java.util.List;


public interface ClassService {

    // 获取班级列表
    List<GradeClass> getClassList();


    PageBean page(Integer page, Integer pageSize, String className, String name, Long id, Integer sortByAwards);

    // 更新用户的获奖数量
    void updateAwardCount(Long userId);
}
