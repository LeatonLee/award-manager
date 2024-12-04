package com.example.service;

import com.example.pojo.GradeClass;
import com.example.pojo.PageBean;

import java.util.List;


public interface ClassService {

    // 获取班级列表
    List<GradeClass> getClassList();

    PageBean page(Integer page, Integer pageSize, String className);
}
