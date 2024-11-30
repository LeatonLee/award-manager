package com.example.mapper;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名和密码查询员工
     * @param user
     * @return
     */
    @Select("select * from user where id = #{id} and password = #{password}")
    User getByIdAndPassword(User user);

    /**
     * 注册
     * @param name
     * @return
     */
    @Select("SELECT * FROM user WHERE name = #{name}")
    User findByName(String name);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);

    @Insert("INSERT INTO user (id,name, grade_class, phone, password, role) " +
            "VALUES (#{id},#{name}, #{gradeClass}, #{phone}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveUser(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
}
