package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.ActorResume;
import com.war4.pojo.DirectorResume;
import com.war4.pojo.ResumeWorItem;
import com.war4.pojo.SeceenwriterResume;
import com.war4.service.ActorResumeService;
import com.war4.service.DirectorResumeService;
import com.war4.service.ResumeWorItemService;
import com.war4.service.SeceenwriterResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/12/21.
 */
@Controller
@RequestMapping(value = "/Resume")
public class ResumeController extends BaseController {


    //保存演员简历
    @RequestMapping(value = "saveActorResume",method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveActorResume(@ModelAttribute ActorResume insetObj){
        ActorResume actorResume = actorResumeService.saveActorResume(insetObj);
        return new ObjectResponse<>(actorResume);
    }

    //编剧简历列表
    @RequestMapping(value = "queryActorResumeList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryActorResumeList(Integer page,Integer limit){
        CutPage cutPage = actorResumeService.queryActorResumeList(page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //查看演员简历
    @RequestMapping(value = "queryActorResume",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryActorResume(String fkUserId){
        ActorResume actorResume = actorResumeService.queryActorResume(fkUserId);
        return new ObjectResponse<>(actorResume);
    }

    //导演简历列表
    @RequestMapping(value = "queryDirectorResumeList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryDirectorResumeList(Integer page,Integer limit){
        CutPage cutPage = directorResumeService.queryDirectorResumeList(page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //保存导演简历
    @RequestMapping(value = "saveDirectorResume",method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveDirectorResume(@ModelAttribute DirectorResume insetObj){
        DirectorResume directorResume = directorResumeService.saveDirectorResume(insetObj);
        return new ObjectResponse<>(directorResume);
    }

    //查看导演简历
    @RequestMapping(value = "queryDirectorResumeById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryDirectorResumeById(String fkUserId){
        DirectorResume actorResume = directorResumeService.queryDirectorResumeById(fkUserId);
        return new ObjectResponse<>(actorResume);
    }

    //保存编剧简历
    @RequestMapping(value = "saveSeceenwriterResume",method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveSeceenwriterResume(@ModelAttribute SeceenwriterResume insetObj){
        SeceenwriterResume seceenwriterResume = seceenwriterResumeService.saveSeceenwriterResume(insetObj);
        return new ObjectResponse<>(seceenwriterResume);
    }

    //查看编剧简历
    @RequestMapping(value = "querySeceenwriterResumeForId",method = RequestMethod.POST)
    public
    @ResponseBody
    Response querySeceenwriterResumeForId(String fkUserId){
        SeceenwriterResume seceenwriterResume = seceenwriterResumeService.querySeceenwriterResumeForId(fkUserId);
        return new ObjectResponse<>(seceenwriterResume);
    }
    //编剧简历列表
    @RequestMapping(value = "querySeceenwriterResumeList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response querySeceenwriterResumeList(Integer page,Integer limit){
        CutPage cutPage = seceenwriterResumeService.querySeceenwriterResumeList(page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //添加简历作品详情
    @RequestMapping(value = "addResumeWorItem",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addResumeWorItem(ResumeWorItem resumeWorItem){
        ResumeWorItem item = resumeWorItemService.addResumeWorItem(resumeWorItem);
        return new ObjectResponse<>(item);
    }

    //查看简历作品详情
    @RequestMapping(value = "queryResumeWorItemDetailById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryResumeWorItemDetailById(String itemId){
        ResumeWorItem item = resumeWorItemService.queryResumeWorItemDetailById(itemId);
        return new ObjectResponse<>(item);
    }

    //查看导演简历作品列表
    @RequestMapping(value = "queryResumeWorItemDirectorListByType",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryResumeWorItemDirectorListByType(String userId,Integer page,Integer limit){
        CutPage cutPage = resumeWorItemService.queryResumeWorItemDirectorListByType(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //查看编剧简历作品列表
    @RequestMapping(value = "queryResumeWorItemSeceenwriterListByType",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryResumeWorItemSeceenwriterListByType(String userId,Integer page,Integer limit){
        CutPage cutPage = resumeWorItemService.queryResumeWorItemSeceenwriterListByType(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }
    //查看演员简历作品列表
    @RequestMapping(value = "queryResumeWorItemPerformeristByType",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryResumeWorItemPerformeristByType(String userId,Integer page,Integer limit){
        CutPage cutPage = resumeWorItemService.queryResumeWorItemPerformeristByType(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //删除简历作品
    @RequestMapping(value = "deleteResumeWorItem",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteResumeWorItem(String itemId){
        resumeWorItemService.deleteResumeWorItem(itemId);
        return new Response("删除成功！");
    }

}
