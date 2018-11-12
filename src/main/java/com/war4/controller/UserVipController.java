package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.UserVipSetting;
import com.war4.service.UserVipSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/1/6.
 */
@Controller
@RequestMapping(value = "/vip")
public class UserVipController  extends BaseController {


    //添加一个会员等级
    @RequestMapping(value = "addUserVipSetting",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addUserVipSetting(@ModelAttribute  UserVipSetting userVipSetting){
        userVipSettingService.addUserVipSetting(userVipSetting);
         return new Response("添加成功！");
    }
    //修改一个会员等级
    @RequestMapping(value = "updateUserVipSetting",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateUserVipSetting(@ModelAttribute  UserVipSetting userVipSetting){
        userVipSettingService.updateUserVipSetting(userVipSetting);
        return new Response("修改成功！");
    }

    //删除一个会员等级
    @RequestMapping(value = "deleteUserVipSetting",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteUserVipSetting(String settingId){
        userVipSettingService.deleteUserVipSetting(settingId);
        return new Response("删除成功！");
    }

    //查看会员等级列表
    @RequestMapping(value = "queryUserVipSetting",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryUserVipSetting(){
        CutPage cutPage =  userVipSettingService.queryUserVipSetting();
        return new ObjectResponse<>(cutPage);
    }

}
