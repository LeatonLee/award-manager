package com.example.service.impl;

import com.example.mapper.ClassMapper;
import com.example.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<GradeClass> getClassList() {
        return classMapper.getAllClasses();
    }

    @Override
    public PageBean page(Integer page, Integer pageSize, String className, String name, Long id, Integer sortByAwards) {
        // 使用 PageHelper 启动分页
        PageHelper.startPage(page, pageSize);

        // 调用 Mapper 层的查询方法，传递查询条件
        List<User> userList = classMapper.listWithSearch(className, name, id, sortByAwards);

        // 转换为 Page 对象以便获取总数和分页数据
        Page<User> u = (Page<User>) userList;

        // 将结果封装到 PageBean 中返回
        PageBean pageBean = new PageBean(u.getTotal(), u.getResult());
        return pageBean;
    }


    @Override
    public void updateAwardCount(Long userId) {

        // 获取该用户的获奖数量
        int awardCount = classMapper.getAwardCountByUserId(userId);

        // 更新用户的获奖数量
        classMapper.updateAwardCount(userId, awardCount);
    }

    @Override
    public void updateGradeClass(Long userId, String gradeClass) {
        userMapper.updateGradeClass(userId, gradeClass);
    }
}
