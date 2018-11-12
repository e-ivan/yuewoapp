package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.ProjectAppointment;

/**
 * Created by Administrator on 2016/12/22.
 */
public interface ProjectAppointmentService {
    ProjectAppointment saveProjectAppointment(ProjectAppointment projectAppointment);
    ProjectAppointment queryProjectAppointmentById(String projectAppointmentId);
    void deleteProjectAppointment(String projectAppointmentId);
    CutPage<ProjectAppointment> queryProjectAppointmentList(String projectId,Integer page,Integer limit);
}
