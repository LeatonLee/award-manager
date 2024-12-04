package com.example.service.impl;

import com.example.mapper.ClassMapper;
import com.example.pojo.GradeClass;
import com.example.pojo.PageBean;
import com.example.pojo.User;
import com.example.service.ClassService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    public void addMember(User user) {
        classMapper.insertMember(user);
    }


    @Override
    public List<GradeClass> getClassList() {
        return classMapper.getAllClasses();
    }

    public PageBean page(Integer page, Integer pageSize, String className) {
        PageHelper.startPage(page,pageSize);

        List<User> userList = classMapper.list(className);
        Page<User> u = (Page<User>) userList;

        PageBean pageBean = new PageBean(u.getTotal(),u.getResult());
        return pageBean;
    }
}
