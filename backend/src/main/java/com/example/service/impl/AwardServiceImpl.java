package com.example.service.impl;

import com.example.mapper.AwardMapper;
import com.example.mapper.ClassMapper;
import com.example.pojo.Award;
import com.example.pojo.AwardRequest;
import com.example.pojo.PageBean;
import com.example.service.AwardService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwardServiceImpl implements AwardService {

    @Autowired
    private AwardMapper awardMapper;

    @Autowired
    private ClassMapper classMapper;
    /**
     * 添加奖项信息
     * @param awardRequest 奖项请求信息
     * @param userId 用户ID
     * @return
     */
    @Override
    public boolean addAward(AwardRequest awardRequest, Long userId) {
        Award award = new Award();
        award.setUserId(userId);
        award.setAwardName(awardRequest.getAwardName());
        award.setAwardLevel(awardRequest.getAwardLevel());
        award.setAwardDate(awardRequest.getAwardDate());
        award.setCertificateUrl(awardRequest.getCertificateUrl());

        // 1. 插入获奖记录
        int result = awardMapper.addAward(award);

        // 2. 更新获奖总数
        int awardCount = classMapper.getAwardCountByUserId(userId);
        classMapper.updateAwardCount(userId, awardCount);

        return result > 0;
    }

    @Override
    public PageBean getAwardList(Integer page, Integer pageSize, String className) {
        PageHelper.startPage(page, pageSize);
        List<Award> awards = awardMapper.getAwardListByClassName(className);
        PageInfo<Award> pageInfo = new PageInfo<>(awards);
        return new PageBean(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void deleteAward(Long id) {
        // 1. 查询奖项对应的用户ID
        Long userId = awardMapper.getUserIdByAwardId(id);
        // 2. 删除奖项
        awardMapper.deleteAward(id);
        // 3. 更新获奖总数
        int awardCount = classMapper.getAwardCountByUserId(userId);
        classMapper.updateAwardCount(userId, awardCount);
    }
}