package com.example.mapper;

import com.example.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {


    @Insert("INSERT INTO user (id, name, grade_class, phone, password, role, created_at, updated_at) " +
            "VALUES (#{id}, #{name}, #{gradeClass}, #{phone}, #{password}, #{role}, #{createdAt}, #{updatedAt})")
    void saveUser(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Update("UPDATE user SET grade_class = #{gradeClass} WHERE id = #{userId}")
    void updateGradeClass(@Param("userId") Long userId, @Param("gradeClass") String gradeClass);
}
