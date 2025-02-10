package com.example.mapper;

import com.example.pojo.Award;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AwardMapper {

    @Insert("INSERT INTO award (user_id, award_name, award_level, award_date, certificate_url, created_at, updated_at) " +
            "VALUES (#{userId}, #{awardName}, #{awardLevel}, #{awardDate},  #{certificateUrl}, NOW(), NOW())")
    int addAward(Award award);

    @Select("SELECT a.*, u.name FROM award a JOIN user u ON a.user_id = u.id WHERE u.grade_class = #{className}")
    List<Award> getAwardListByClassName(String className);

    @Delete("DELETE FROM award WHERE id = #{id}")
    void deleteAward(Long id);

    @Select("SELECT user_id FROM award WHERE id = #{id}")
    Long getUserIdByAwardId(Long id);

    List<Award> selectByUserId(Map<String, Object> params);

}
