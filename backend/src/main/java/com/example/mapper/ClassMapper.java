package com.example.mapper;

import com.example.pojo.GradeClass;
import com.example.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ClassMapper {

    /**
     * 查询所有班级信息
     * @return
     */
    @Select("SELECT * FROM gradeclass")
    List<GradeClass> getAllClasses();

    void insertMember(User user);

    void updateMember(User user);

    void deleteMember(Long id);

    List<User> list(String className);


    /**
     * 获取某个用户的获奖数量
     * @param userId
     * @return
     */
    @Select("SELECT COUNT(*) FROM award WHERE user_id = #{userId}")
    int getAwardCountByUserId(Long userId);

    /**
     * 更新用户的获奖数量
     * @param userId
     * @param awardCount
     */
    @Update("UPDATE user SET award_count = #{awardCount} WHERE id = #{userId}")
    void updateAwardCount(Long userId, int awardCount);

    List<User> listWithSearch(String className, String name, Long id, Integer sortByAwards);
}
