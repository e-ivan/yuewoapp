package com.war4.controller;

import com.war4.base.*;
import com.war4.enums.UserRoleType;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dly on 2016/8/8.
 * 消息中心
 */
@Controller
@RequestMapping(value = "messageCenter")
public class MessageCenterController extends BaseController{




    //-----------------------------------好友申请----------------------------------------
    //好友申请---发送
    @RequestMapping(value = "sendMakeFriendApply")
    public @ResponseBody
    Response
    sendMakeFriendApply(String fromUserId,String toUserId,String content){
        messageCenterService.sendMakeFriendApply(fromUserId,toUserId,content,true);
        return new Response("好友申请发送成功");
    }

    //好友申请---处理
    @RequestMapping(value = "resolveMakeFriendApply")
    public @ResponseBody
    Response
    resolveMakeFriendApply(String makeFriendApplyId,Integer result){
        messageCenterService.resolveMakeFriendApply(makeFriendApplyId,result);
        return new Response("状态处理成功");
    }


    //好友申请---列表查询
    @RequestMapping(value = "queryMakeFriendApplyList")
    public @ResponseBody
    Response
    queryMakeFriendApplyList(String myUserBaseId,Integer page,Integer limit){
        CutPage<MessageFriendApply> cutPage = messageCenterService.queryMakeFriendApplyList(myUserBaseId,page,limit);
        return new ObjectResponse<>(cutPage);
    }

    //好友申请---列表查询
    @RequestMapping(value = "deleteMessageApply")
    public @ResponseBody
    Response
    deleteMessageApply(String applyId){
        messageCenterService.deleteMessageApply(applyId);
        return new Response("删除成功！");
    }

}
