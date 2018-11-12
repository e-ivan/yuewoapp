package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.Gift;
import com.war4.service.GiftService;
import com.war4.service.UserGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/12/15.
 */
@Controller
@RequestMapping(value = "gift")
public class GiftController  extends BaseController{


    //添加礼物
    @RequestMapping(value = "addGift",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addGift(Gift gift){
        return new ObjectResponse<>(giftService.addGift(gift));
    }

    //礼物列表
    @RequestMapping(value = "queryGift",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryGift(Integer page,Integer limit){
        return new ObjectResponse<>(giftService.queryGift(page,limit));
    }

    //删除礼物
    @RequestMapping(value = "deleteGift",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteGift(String giftId){
        giftService.deleteGift(giftId);
        return new Response("删除成功！");
    }

    //赠送礼物
    @RequestMapping(value = "addUserGift",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addUserGift(String userId, String acceptUserId, String giftId, Integer count,String orderId){
        userGiftService.addUserGift(userId, acceptUserId, giftId, count,orderId);
        return new Response("赠送成功！");
    }

    //查看我的礼物
    @RequestMapping(value = "queryMyGetGift",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyGetGift(String userId, Integer page, Integer limit){
        CutPage cutPage = userGiftService.queryMyGetGift(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //查看我送出的礼物
    @RequestMapping(value = "queryMySendGift",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMySendGift(String userId, Integer page, Integer limit){
        CutPage cutPage = userGiftService.queryMySendGift(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }
}
