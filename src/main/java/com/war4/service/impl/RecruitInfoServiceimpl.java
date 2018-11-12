package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.RecruitType;
import com.war4.pojo.RecruitInfo;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.RecruitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/22.
 */
@Service
public class RecruitInfoServiceimpl implements RecruitInfoService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;


    @Override
    @Transactional
    public RecruitInfo saveRecruitInfo(RecruitInfo recruitInfo) {
        if(recruitInfo.getFkCrewId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"招聘的剧组Id不能为空！");
        }
        if(recruitInfo.getApplyType()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"招聘的角色类型不能为空！");
        }
        if(RecruitType.getByValue(recruitInfo.getApplyType())==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的招聘的角色类型！");
        }
        if(recruitInfo.getId()==null){
            recruitInfo.setId(UUID.randomUUID().toString());
            baseRepository.saveObj(recruitInfo);
            return recruitInfo;
        }
        RecruitInfo selectObj = baseRepository.getObjById(RecruitInfo.class,recruitInfo.getId());
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到这个剧组");
        }
        selectObj.setFkCrewId(recruitInfo.getFkCrewId());
        selectObj.setRoleName(recruitInfo.getRoleName());
        selectObj.setRoleAge(recruitInfo.getRoleAge());
        selectObj.setRoleSex(recruitInfo.getRoleSex());
        selectObj.setRoleIntro(recruitInfo.getRoleIntro());
        selectObj.setRoleLabel(recruitInfo.getRoleLabel());
        selectObj.setApplyType(recruitInfo.getApplyType());
        selectObj.setCount(recruitInfo.getCount());
        baseRepository.updateObj(selectObj);

        return selectObj;
    }

    @Override
    public RecruitInfo queryRecruitInfoById(String recruitInfoId) {
        RecruitInfo selectObj = baseRepository.getObjById(RecruitInfo.class,recruitInfoId);
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到这个剧组");
        }
        selectObj = assembleService.assembleObject(selectObj);
        return selectObj;
    }

    @Override
    @Transactional
    public void deleteRecruitInfoById(String recruitInfoId) {
        RecruitInfo selectObj = baseRepository.getObjById(RecruitInfo.class,recruitInfoId);
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到这个剧组");
        }
        baseRepository.logicDelete(selectObj);
    }

    @Override
    public CutPage<RecruitInfo> queryRecruitInfoListForCrew(String crewId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(RecruitInfo.class);
        hql += " and fkCrewId = '"+crewId+"'";
        hql += " order by createTime desc";
        CutPage<RecruitInfo> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (RecruitInfo recruitInfo :cutPage.getiData()){
            recruitInfo = assembleService.assembleObject(recruitInfo);
        }
        return cutPage;
    }
}
