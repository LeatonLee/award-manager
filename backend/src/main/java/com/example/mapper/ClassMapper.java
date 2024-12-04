package com.example.mapper;

import com.example.pojo.GradeClass;
import com.example.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassMapper {

    // 查询所有班级信息
    @Select("SELECT * FROM gradeclass")
    List<GradeClass> getAllClasses();

    void insertMember(User user);

    void updateMember(User user);

    void deleteMember(Long id);

    List<User> list(String className);
}
