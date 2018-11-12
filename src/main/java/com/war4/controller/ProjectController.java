package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.Crew;
import com.war4.pojo.Project;
import com.war4.pojo.ProjectAppointment;
import com.war4.repository.BaseRepository;
import com.war4.service.ProjectAppointmentService;
import com.war4.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/12/23.
 */

@Controller
@RequestMapping(value = "/project")
public class ProjectController extends BaseController{

    //保存投资项目
    @RequestMapping(value = "saveProject",method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveProject(@ModelAttribute Project insetObj){
        Project project = projectService.saveProject(insetObj);
        return new ObjectResponse<>(project);
    }

    //投资项目详情
    @RequestMapping(value = "queryProjectById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryProjectById(String projectId){
        Project project = projectService.queryProjectById(projectId);
        return new ObjectResponse<>(project);
    }

    //投资项目详情
    @RequestMapping(value = "deleteProectById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteProectById(String projectId){
        projectService.deleteProectById(projectId);
        return new Response("删除成功！");
    }

    //投资项目列表
    @RequestMapping(value = "queryProjectList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryProjectList(Integer page,Integer limit){
        CutPage cutPage =  projectService.queryProjectList(page, limit);
        return new ObjectResponse<>(cutPage);
    }

//    //保存投资预约
//    @RequestMapping(value = "saveProjectAppointment",method = RequestMethod.POST)
//    public
//    @ResponseBody
//    Response saveProjectAppointment(@ModelAttribute ProjectAppointment insetObj){
//        System.out.println("===========");
//        ProjectAppointment projectAppointment = projectAppointmentService.saveProjectAppointment(insetObj);
//        return new ObjectResponse<>(projectAppointment);
//    }


    //投资预约详情
    @RequestMapping(value = "addProjectAppointment",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addProjectAppointment(@ModelAttribute ProjectAppointment insertObj){
        ProjectAppointment projectAppointment = projectAppointmentService.saveProjectAppointment(insertObj);
        return new ObjectResponse<>(projectAppointment);
    }

    //投资预约详情
    @RequestMapping(value = "queryProjectAppointmentById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryProjectAppointmentById(String projectAppointmentId){
        ProjectAppointment projectAppointment = projectAppointmentService.queryProjectAppointmentById(projectAppointmentId);
        return new ObjectResponse<>(projectAppointment);
    }


    //删除投资预约
    @RequestMapping(value = "deleteProjectAppointment",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteProjectAppointment(String projectAppointmentId){
        projectAppointmentService.deleteProjectAppointment(projectAppointmentId);
        return new Response("删除成功！");
    }

    //删除投资预约
    @RequestMapping(value = "queryProjectAppointmentList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryProjectAppointmentList(String projectId,Integer page,Integer limit){
        CutPage cutPage = projectAppointmentService.queryProjectAppointmentList(projectId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

}
