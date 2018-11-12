package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.RecruitType;
import com.war4.pojo.RecruitApply;
import com.war4.pojo.RecruitInfo;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.RecruitApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/22.
 */

@Service
public class RecruitApplyServiceImpl implements RecruitApplyService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public RecruitApply saveRecruitApply(RecruitApply recruitApply) {
        if(recruitApply.getApplyType()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"申请类型不能为空");
        }
        if(recruitApply.getRecruitId()==null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"RecruitId不能为空");
        }

        if(RecruitType.getByValue(recruitApply.getApplyType())==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的申请类型");
        }

        recruitApply.setId(UUID.randomUUID().toString());
        baseRepository.saveObj(recruitApply);
        return recruitApply;

    }

    @Override
    public RecruitApply queryRecruitApplyById(String applyId) {
        RecruitApply selectObj = baseRepository.getObjById(RecruitApply.class,applyId);
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"这个申请记录不存在");
        }
        selectObj = assembleService.assembleObject(selectObj);
        return selectObj;
    }

    @Override
    @Transactional
    public void deleteRecruitApplyById(String applyId) {
        RecruitApply selectObj = baseRepository.getObjById(RecruitApply.class,applyId);
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"这个申请记录不存在");
        }
        baseRepository.logicDelete(selectObj);
    }

    @Override
    public CutPage<RecruitApply> queryRecruitApplyList(String recruitInfoId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(RecruitApply.class);
        hql += " and recruitId = '"+recruitInfoId+"'";
        hql += " order by createTime desc";
        CutPage<RecruitApply> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(RecruitApply recruitApply:cutPage.getiData()){
            recruitApply = assembleService.assembleObject(recruitApply);
        }
        return cutPage;
    }
}
