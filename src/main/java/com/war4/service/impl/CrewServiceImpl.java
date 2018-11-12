package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.Crew;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.CrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/22.
 */
@Service
public class CrewServiceImpl implements CrewService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public Crew saveCrew(Crew crew) {
        if(crew.getName()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"剧组名不能为空");
        }
        if(crew.getType()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"剧组题材不能为空");
        }
        if(crew.getPublishPlatform()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"发布平台不能为空");
        }
        if(crew.getId()==null){
            crew.setId(UUID.randomUUID().toString());
            baseRepository.saveObj(crew);
            return crew;
        }
        Crew selectObj = baseRepository.getObjById(Crew.class,crew.getId());
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到这个剧组！");
        }
        selectObj.setName(crew.getName());
        selectObj.setType(crew.getType());
        selectObj.setOpenTime(crew.getOpenTime());
        selectObj.setPublishPlatform(crew.getPublishPlatform());
        selectObj.setStartRecruitTime(crew.getStartRecruitTime());
        selectObj.setEndRecruitTime(crew.getEndRecruitTime());
        selectObj.setShootingCycle(crew.getShootingCycle());
        selectObj.setShootingLocation(crew.getShootingLocation());

        baseRepository.updateObj(selectObj);

        return selectObj;
    }

    @Override
    public Crew queryCrewById(String crewId) {
        Crew selectObj = baseRepository.getObjById(Crew.class,crewId);
        if(selectObj!=null){
            selectObj = assembleService.assembleObject(selectObj);
        }
        return selectObj;
    }

    @Override
    @Transactional
    public void deleteCrewById(String crewId) {
        Crew selectObj = baseRepository.getObjById(Crew.class,crewId);
        if(selectObj!=null){
           baseRepository.logicDelete(selectObj);
        }
    }

    @Override
    public CutPage<Crew> queryCrewList(Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(Crew.class);
        CutPage<Crew> crewCutPage = new CutPage<>(page, limit);
        crewCutPage = baseRepository.queryCutPageData(hql,crewCutPage);
        for(Crew crew:crewCutPage.getiData()){
            crew = assembleService.assembleObject(crew);
        }
        return crewCutPage;
    }
}
