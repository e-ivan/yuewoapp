package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.ActivityBid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 投标控制器
 * Created by hh on 2017.7.8 0008.
 */
@Controller
@RequestMapping(value = "activityBid")
public class ActivityBidController extends BaseController {
    private static final String opPassword = "ivan";

    //增加一元购活动
    @RequestMapping(value = "addActivityBid",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addActivityBid(ActivityBid activityBid){
        ActivityBid actBid = activityBidService.addActivityBid(activityBid);
        return new ObjectResponse<>(actBid);
    }

    //删除活动
    @RequestMapping(value = "deleteActivityBid",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteActivityBid(Long id,String password){
        if (opPassword.equals(password)){
            activityBidService.deleteActivityBid(id);
            return new Response("删除成功");
        }
        return new Response("你无访问权限，请联系技术人员！");
    }

    //列表
    @RequestMapping(value = "queryActivityBidList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryActivityBidList(Byte type,Integer state,Integer page, Integer limit){
        CutPage cutPage = activityBidService.queryActivityBidList(type,state,page, limit);
        return new ObjectResponse<>(cutPage);
    }

    /**
     * 获取活动详情
     * @param bId
     * @param userId
     */
    @RequestMapping(value = "getActivityBid",method = RequestMethod.POST)
    public
    @ResponseBody
    Response getActivityBid(Long bId,String userId){
        return new ObjectResponse<>(activityBidService.getActivityBid(bId,true, userId));
    }

    /**
     * 参与活动
     * @param id    活动id
     * @param userId    用户id
     * @param count     参与次数
     */
    @RequestMapping(value = "createActivityBidItem",method = RequestMethod.POST)
    public
    @ResponseBody
    Response createActivityBidItem(Long id, String userId, Integer count){
        Map<String,String> map = new HashMap<>();
        map.put("orderId",activityBidService.createActivityBidItem(id,userId,count));
        return new ObjectResponse<>(map);
    }


}
