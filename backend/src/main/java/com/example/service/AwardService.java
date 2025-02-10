package com.example.service;

import com.example.pojo.AwardRequest;
import com.example.pojo.PageBean;

public interface AwardService {

    /**
     * 添加奖项信息
     * @param awardRequest 奖项请求信息
     * @param userId 用户ID
     * @return 是否添加成功
     */
    boolean addAward(AwardRequest awardRequest, Long userId);

    PageBean getAwardList(Integer page, Integer pageSize, String className);

    void deleteAward(Long id);

    PageBean getUserAwards(Integer page, Integer pageSize, Long userId, String keyword);


}
