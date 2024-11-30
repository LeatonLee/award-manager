package com.example.mapper;

import com.example.pojo.GradeClass;
import com.example.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface ClassMapper {

    // 查询所有班级信息
    @Select("SELECT * FROM gradeclass")
    List<GradeClass> getAllClasses();
    boolean isAdminOfClass(@Param("studentId") Long studentId, @Param("className") String className);

    List<User> findMembersByClassName(@Param("className") String className);

    void insertMember(User user);

    void updateMember(User user);

    void deleteMember(Long id);
}
