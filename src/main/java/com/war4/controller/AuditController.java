package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.vo.AuditParamVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 文章控制器
 * Created by hh on 2017.7.8 0008.
 */
@Controller
@RequestMapping(value = "audit")
public class AuditController extends BaseController {

    /**
     * 审核用户头像
     */
    @RequestMapping(value = "updateUserHeadState")
    public
    @ResponseBody
    Response updateUserHeadState(String userId,Integer state) throws Exception{
        userInfoBaseService.updateUserHeadState(userId,state);
        return new Response("审核成功!");
    }

    /**
     * 竞拍审核
     */
    @RequestMapping(value = "updateAuctionState",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateAuctionState(AuditParamVO vo) throws Exception{
        auctionService.updateAuctionState(vo);
        return new Response(vo.isPass() ? "您已同意该竞拍！" : "您已拒绝该竞拍！");
    }

    /**
     *审核实名认证
     */
    @RequestMapping(value = "updateUserAuthentication",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateUserAuthentication(AuditParamVO vo){
        auditService.updateAuthentication(vo);
        return new ObjectResponse<>("审核成功!");
    }

    /**
     *审核个人视频
     */
    @RequestMapping(value = "updateUserVideo",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateUserVideo(AuditParamVO vo){
        auditService.updateUserVideo(vo);
        return new ObjectResponse<>("审核成功!");
    }

    /**
     *审核红人
     */
    @RequestMapping(value = "updateInternetStar",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateInternetStar(AuditParamVO vo){
        auditService.updateInternetStar(vo);
        return new ObjectResponse<>("审核成功!");
    }

}
