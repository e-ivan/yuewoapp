package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.DirectorResume;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.DirectorResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/12/21.
 */
@Service
public class DirectorResumeServiceImpl implements DirectorResumeService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public DirectorResume saveDirectorResume(DirectorResume directorResume) {
        if(directorResume.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"fkUserId不能为空！");
        }
        DirectorResume selectObj = baseRepository.getObjById(DirectorResume.class,directorResume.getFkUserId());
        if(selectObj==null){
            baseRepository.saveObj(directorResume);
            return  directorResume;
        }
        selectObj.setName(directorResume.getName());
        selectObj.setSex(directorResume.getSex());
        selectObj.setAge(directorResume.getAge());
        selectObj.setConstellation(directorResume.getConstellation());
        selectObj.setWeight(directorResume.getWeight());
        selectObj.setHeight(directorResume.getHeight());
        selectObj.setCity(directorResume.getCity());
        selectObj.setSchool(directorResume.getSchool());
        selectObj.setMajor(directorResume.getMajor());
        selectObj.setPersonLabel(directorResume.getPersonLabel());
        selectObj.setIsPublic(directorResume.getIsPublic());
        baseRepository.updateObj(selectObj);

        return selectObj;
    }

    @Override
    public DirectorResume queryDirectorResumeById(String fkUserId) {
        DirectorResume selectObj = baseRepository.getObjById(DirectorResume.class,fkUserId);
        if(selectObj!=null){
           selectObj = assembleService.assembleObject(selectObj);
        }
        return selectObj;
    }

    @Override
    public CutPage<DirectorResume> queryDirectorResumeList(Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(DirectorResume.class);
        hql += " order by createTime desc";
        CutPage<DirectorResume> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(DirectorResume directorResume : cutPage.getiData()){
            directorResume = assembleService.assembleObject(directorResume);
        }
        return cutPage;
    }
}
