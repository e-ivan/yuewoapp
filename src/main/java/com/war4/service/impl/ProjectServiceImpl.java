package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.Project;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/22.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public Project saveProject(Project project) {

        if(project.getId()==null){
            project.setId(UUID.randomUUID().toString());
            baseRepository.saveObj(project);
            return project;
        }
        Project selectObj = baseRepository.getObjById(Project.class,project.getId());
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"投资项目不存在");
        }
        selectObj.setProjectIntro(project.getProjectIntro());
        selectObj.setProjectMoney(project.getProjectMoney());
        selectObj.setProjectName(project.getProjectName());
        selectObj.setProjectLaunch(project.getProjectLaunch());
        baseRepository.updateObj(selectObj);

        return selectObj;
    }

    @Override
    public Project queryProjectById(String projectId) {
        Project selectObj = baseRepository.getObjById(Project.class,projectId);
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"投资项目不存在");
        }
        selectObj  = assembleService.assembleObject(selectObj);
        return selectObj;
    }

    @Override
    @Transactional
    public void deleteProectById(String projectId) {
        Project selectObj = baseRepository.getObjById(Project.class,projectId);
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"投资项目不存在");
        }
        baseRepository.logicDelete(selectObj);
    }

    @Override
    public CutPage<Project> queryProjectList(Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(Project.class);
        hql += " order by createTime desc ";
        CutPage<Project> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(Project project:cutPage.getiData()){
            project = assembleService.assembleObject(project);
        }
        return cutPage;
    }
}
