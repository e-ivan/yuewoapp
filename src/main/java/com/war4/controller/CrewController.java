package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.ActorResume;
import com.war4.pojo.Crew;
import com.war4.pojo.RecruitApply;
import com.war4.pojo.RecruitInfo;
import com.war4.service.CrewService;
import com.war4.service.RecruitApplyService;
import com.war4.service.RecruitInfoService;
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
@RequestMapping(value = "/crew")
public class CrewController extends BaseController {

    //保存剧组
    @RequestMapping(value = "saveCrew",method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveCrew(@ModelAttribute Crew insetObj){
        Crew crew = crewService.saveCrew(insetObj);
        return new ObjectResponse<>(crew);
    }

    //剧组详情
    @RequestMapping(value = "queryCrewById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCrewById(String crewId){
        Crew crew = crewService.queryCrewById(crewId);
        return new ObjectResponse<>(crew);
    }

    //剧组删除
    @RequestMapping(value = "deleteCrewById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteCrewById(String crewId){
         crewService.deleteCrewById(crewId);
        return new Response("删除成功！");
    }

    //剧组列表
    @RequestMapping(value = "queryCrewList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCrewList(Integer page,Integer limit){
        CutPage cutPage = crewService.queryCrewList(page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //保存招聘信息
    @RequestMapping(value = "saveRecruitInfo",method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveRecruitInfo(@ModelAttribute RecruitInfo insetObj){
        RecruitInfo recruitInfo = recruitInfoService.saveRecruitInfo(insetObj);
        return new ObjectResponse<>(recruitInfo);
    }

    //招聘信息详情
    @RequestMapping(value = "queryRecruitInfoById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryRecruitInfoById(String recruitInfoId){
        RecruitInfo recruitInfo = recruitInfoService.queryRecruitInfoById(recruitInfoId);
        return new ObjectResponse<>(recruitInfo);
    }

    //删除招聘信息
    @RequestMapping(value = "deleteRecruitInfoById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteRecruitInfoById(String recruitInfoId){
        recruitInfoService.deleteRecruitInfoById(recruitInfoId);
        return new Response("删除成功！");
    }

    //招聘信息列表
    @RequestMapping(value = "queryRecruitInfoListForCrew",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryRecruitInfoListForCrew(String crewId,Integer page,Integer limit){
        CutPage cutPage = recruitInfoService.queryRecruitInfoListForCrew(crewId, page, limit);
        return new ObjectResponse<>(cutPage);
    }


    //保存招聘申请
    @RequestMapping(value = "saveRecruitApply",method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveRecruitApply(@ModelAttribute RecruitApply insetObj){
        RecruitApply recruitApply = recruitApplyService.saveRecruitApply(insetObj);
        return new ObjectResponse<>(recruitApply);
    }

    //保存招聘详情
    @RequestMapping(value = "queryRecruitApplyById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryRecruitApplyById(String applyId){
        RecruitApply recruitApply = recruitApplyService.queryRecruitApplyById(applyId);
        return new ObjectResponse<>(recruitApply);
    }

    //删除招聘
    @RequestMapping(value = "deleteRecruitApplyById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteRecruitApplyById(String applyId){
        recruitApplyService.deleteRecruitApplyById(applyId);
        return new Response("删除招聘成功！");
    }

    //删除招聘
    @RequestMapping(value = "queryRecruitApplyList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryRecruitApplyList(String recruitInfoId,Integer page,Integer limit){
        CutPage cutPage = recruitApplyService.queryRecruitApplyList(recruitInfoId, page, limit);
        return new ObjectResponse<>(cutPage);
    }
}
