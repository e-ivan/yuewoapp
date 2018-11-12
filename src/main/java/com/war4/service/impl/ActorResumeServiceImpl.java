package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.ResumeIsPublic;
import com.war4.pojo.ActorResume;
import com.war4.repository.BaseRepository;
import com.war4.service.ActorResumeService;
import com.war4.service.AssembleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/12/21.
 */
@Service
public class ActorResumeServiceImpl implements ActorResumeService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public ActorResume saveActorResume(ActorResume actorResume) {
        if(actorResume.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"fkUserId不能为空！");
        }
        ActorResume selectObj = baseRepository.getObjById(ActorResume.class,actorResume.getFkUserId());
        if(actorResume.getIsPublic()!=null){
            if(ResumeIsPublic.getByCode(actorResume.getIsPublic())==null){
                throw new BusinessException(CommonErrorResult.SECRET_FAIL,"简历是否公开只能是0或者1");
            }
        }
        if(selectObj==null){
            baseRepository.saveObj(actorResume);
            return actorResume;
        }
        selectObj.setName(actorResume.getName());
        selectObj.setSex(actorResume.getSex());
        selectObj.setAge(actorResume.getAge());
        selectObj.setConstellation(actorResume.getConstellation());
        selectObj.setWeight(actorResume.getWeight());
        selectObj.setHeight(actorResume.getHeight());
        selectObj.setCity(actorResume.getCity());
        selectObj.setSchool(actorResume.getSchool());
        selectObj.setMajor(actorResume.getMajor());
        selectObj.setAudition(actorResume.getAudition());
        selectObj.setSpecialty(actorResume.getSpecialty());
        selectObj.setPersonLabel(actorResume.getPersonLabel());
        selectObj.setPerformingArtsExperience(actorResume.getPerformingArtsExperience());
        selectObj.setIsPublic(actorResume.getIsPublic());
        baseRepository.updateObj(selectObj);
        return actorResume;
    }

    @Override
    public ActorResume queryActorResume(String fkUserId) {
        ActorResume selectObj = baseRepository.getObjById(ActorResume.class,fkUserId);
        if(selectObj!=null){
            selectObj = assembleService.assembleObject(selectObj);
        }
        return selectObj;
    }

    @Override
    public CutPage<ActorResume> queryActorResumeList(Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(ActorResume.class);
        hql += " order by createTime desc";
        CutPage<ActorResume> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(ActorResume actorResume :cutPage.getiData()){
            actorResume = assembleService.assembleObject(actorResume);
        }
        return cutPage;
    }
}
