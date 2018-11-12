package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.ProjectAppointment;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.ProjectAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/22.
 */
@Service
public class ProjectAppointmentServiceImpl implements ProjectAppointmentService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public ProjectAppointment saveProjectAppointment(ProjectAppointment projectAppointment) {
        if(projectAppointment.getProjectId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"projectId不能为空！");
        }

        if(projectAppointment.getId()==null){
            projectAppointment.setId(UUID.randomUUID().toString());
            baseRepository.saveObj(projectAppointment);
            return projectAppointment;
        }
        return projectAppointment;
    }

    @Override
    public ProjectAppointment queryProjectAppointmentById(String projectAppointmentId) {

        ProjectAppointment selectObj = baseRepository.getObjById(ProjectAppointment.class,projectAppointmentId);
        if(selectObj!=null){
            selectObj = assembleService.assembleObject(selectObj);
        }
        return selectObj;
    }

    @Override
    @Transactional
    public void deleteProjectAppointment(String projectAppointmentId) {
        ProjectAppointment selectObj = baseRepository.getObjById(ProjectAppointment.class,projectAppointmentId);
        if(selectObj==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到该条投资预约表");
        }
    }

    @Override
    public CutPage<ProjectAppointment> queryProjectAppointmentList(String projectId, Integer page, Integer limit) {

        String hql = baseRepository.getBaseHqlByClass(ProjectAppointment.class);
        hql +=" and projectId = '"+projectId+"'";
        CutPage<ProjectAppointment> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(ProjectAppointment projectAppointment:cutPage.getiData()){
            projectAppointment = assembleService.assembleObject(projectAppointment);
        }
        return cutPage;
    }
}
