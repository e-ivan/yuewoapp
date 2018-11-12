package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.DirectorResume;
import com.war4.pojo.SeceenwriterResume;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.SeceenwriterResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/12/21.
 */
@Service
public class SeceenwriterResumeServiceImpl implements SeceenwriterResumeService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;


    @Override
    @Transactional
    public SeceenwriterResume saveSeceenwriterResume(SeceenwriterResume seceenwriterResume) {
        if(seceenwriterResume.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"fkUserId不能为空！");
        }
        SeceenwriterResume selectObj = baseRepository.getObjById(SeceenwriterResume.class,seceenwriterResume.getFkUserId());
        if(selectObj==null){
            baseRepository.saveObj(seceenwriterResume);
            return  seceenwriterResume;
        }
        selectObj.setName(seceenwriterResume.getName());
        selectObj.setSex(seceenwriterResume.getSex());
        selectObj.setAge(seceenwriterResume.getAge());
        selectObj.setConstellation(seceenwriterResume.getConstellation());
        selectObj.setWeight(seceenwriterResume.getWeight());
        selectObj.setHeight(seceenwriterResume.getHeight());
        selectObj.setCity(seceenwriterResume.getCity());
        selectObj.setSchool(seceenwriterResume.getSchool());
        selectObj.setMajor(seceenwriterResume.getMajor());
        selectObj.setPersonLabel(seceenwriterResume.getPersonLabel());
        selectObj.setIsPublic(seceenwriterResume.getIsPublic());
        baseRepository.updateObj(selectObj);

        return selectObj;
    }

    @Override
    public SeceenwriterResume querySeceenwriterResumeForId(String fkUserId) {
        SeceenwriterResume selectObj = baseRepository.getObjById(SeceenwriterResume.class,fkUserId);
        if(selectObj!=null){
           selectObj = assembleService.assembleObject(selectObj);
        }
        return selectObj;
    }

    @Override
    public CutPage<SeceenwriterResume> querySeceenwriterResumeList(Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(SeceenwriterResume.class);
        hql += " order by createTime desc";
        CutPage<SeceenwriterResume> cutPage = new CutPage<>(page,limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (SeceenwriterResume seceenwriterResume:
            cutPage.getiData() ) {
            seceenwriterResume = assembleService.assembleObject(seceenwriterResume);
        }
        return cutPage;
    }
}
