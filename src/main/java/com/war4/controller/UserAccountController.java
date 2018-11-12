package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.Cash;
import com.war4.pojo.UserAccount;
import com.war4.pojo.UserCashAccount;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/14.
 */
@Controller
@RequestMapping(value = "account")
public class UserAccountController  extends BaseController {


    //查看账户
    @RequestMapping(value = "queryUserAccountByUserId",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryUserAccountByUserId(String userId){
        UserAccount userAccount = userAccountService.getUserAccountByUserId(userId);
        return new ObjectResponse<>(userAccount);
    }

    //创建充值订单
    @RequestMapping(value = "addRecharge",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addRecharge(String userId, BigDecimal money){
        return new ObjectResponse<>(rechargeOrderService.createRechargeOrder(userId, money));
    }

    //取消充值订单
    @RequestMapping(value = "cancelRecharge",method = RequestMethod.POST)
    public
    @ResponseBody
    Response cancelRecharge(String orderId){
        rechargeOrderService.cancelRechargeOrder(orderId);
        return new Response("取消成功");
    }

    //充值记录列表
    @RequestMapping(value = "queryRechargeListByUserId",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryRechargeListByUserId(String userId, Integer page, Integer limit) throws Exception{
        return new ObjectResponse<>(rechargeOrderService.queryUserRechargeOrder(userId, page, limit));
    }

    //提现提交
    @RequestMapping(value = "addCash",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addCash(Cash cash,String payPassword){
        userInfoBaseService.checkPayPassword(cash.getApplierId(),payPassword);
        cashService.addCash(cash);
        return new Response("您的提现已提交！");
    }

    //提现同意
    @RequestMapping(value = "updateCashSuccess",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateCashSuccess(Cash cash){
        cashService.updateCashStatus(cash,true);
        return new Response("您已经同意该笔提现！");
    }

    //提现拒绝
    @RequestMapping(value = "updateCashRefuse",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateCashRefuse(Cash cash){
        cashService.updateCashStatus(cash,false);
        return new Response("您已经拒绝该笔提现！");
    }

    //个人提现列表
    @RequestMapping(value = "queryCashForUser",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCashForUser(String fkUserId, Integer page, Integer limit){
        CutPage cutPage =  cashService.queryCashForUser(fkUserId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //所有提现列表
    @RequestMapping(value = "queryCashForAll",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCashForAll(Integer state,Integer accountType,String keyword,Integer page, Integer limit){
        return new ObjectResponse<>(cashService.queryCashForAll(state,accountType,keyword,page, limit));
    }

    //所有提现列表
    @RequestMapping(value = "addgold",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addgold(String userId,BigDecimal glod){
       userAccountDetailService.addgold(userId,glod);
        return new Response("添加");
    }

    //用户账户流水
    @RequestMapping(value = "queryUserAccountStatement",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryUserAccountStatement(String userId, Integer amountType, Integer dealType, Integer page, Integer limit){
        return new ObjectResponse<>(userAccountService.queryUserAccountStatement(userId,amountType, dealType,page,limit));
    }

    //金币转换为余额
    @RequestMapping(value = "goldTransformationBalance",method = RequestMethod.POST)
    public
    @ResponseBody
    Response goldTransformationBalance(String userId, BigDecimal gold){
        userAccountService.goldTransformationBalance(userId,gold);
        return new Response("提交成功！");
    }

    //绑定提现账户
    @RequestMapping(value = "bindCashAccount",method = RequestMethod.POST)
    public
    @ResponseBody
    Response bindCashAccount(UserCashAccount userCashAccount,String payPassword){
        userInfoBaseService.checkPayPassword(userCashAccount.getUserId(),payPassword);
        userCashAccountService.updateUserCashAccount(userCashAccount);
        return new Response("已保存");
    }

    //获取提现账户信息
    @RequestMapping(value = "getCashAccountByUser",method = RequestMethod.POST)
    public
    @ResponseBody
    Response getCashAccountByUser(String userId){
        return new ObjectResponse<>(userCashAccountService.getCashAccountByUser(userId));
    }

    //余额支付
    @RequestMapping(value = "balancePay",method = RequestMethod.POST)
    public
    @ResponseBody
    Response balancePay(String userId,String orderId, String payPassword) throws Exception{
        userInfoBaseService.checkPayPassword(userId,payPassword);
        payUtilService.balancePayOrder(userId, orderId);
        return new Response("支付成功");
    }

}
