package com.war4.controller;

import com.war4.base.*;
import com.war4.enums.*;
import com.war4.pojo.*;
import com.war4.util.DateUtil;
import com.war4.util.NumberUtil;
import com.war4.util.UserContext;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * Created by dly on 2016/7/27.
 */
@Controller
@RequestMapping(value = "user")

public class UserBaseController extends BaseController {

    private static final int userJoinCount = PropertiesConfig.getUserJoinCount();

    //注册
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public
    @ResponseBody
    Response register(UserInfoBase userInfoBase, String validCode) {
        tempValidCodeService.checkValidCode(userInfoBase.getPhone(), validCode);
        userInfoBase.setRoleType(UserRoleType.COMMON_USER.getCode());
        UserInfoBase user = userInfoBaseService.insertUserBase(userInfoBase);
        return new ObjectResponse<>(user);
    }

    @RequestMapping(value = "findUserInfo", method = RequestMethod.POST)
    public
    @ResponseBody
    Response findUserInfo(String phone) {
        userInfoBaseService.findUserInfo(phone);
        return new Response("成功");
    }


    //登陆
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public
    @ResponseBody
    Response login(HttpServletRequest request,String userName, String password, String roleType) {
        UserInfoBase user = userInfoBaseService.checkLoginParam(userName, password, roleType);
        if (user != null) {//如果验证通过
            if (!user.getStatus().equals(UserStatus.NORMAL.getCode())) {
                return new Response(CommonErrorResult.BUSINESS_ERROR, "您的账号异常，请联系客服人员");
            }
            request.getSession().setAttribute(UserContext.USER_IN_SESSION,user);
            return new ObjectResponse<>(user);
        } else {
            return new Response(CommonErrorResult.BUSINESS_ERROR, "验证失败");
        }
    }

    /**
     * 保存用户客户端信息
     */
    @RequestMapping(value = "saveLoginClient", method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveLoginClient(String userId, String imei, String model, String os, String rid) {
        userClientRecordService.saveUserClient(userId, imei, model, os, rid);
        return new Response("保存成功！");
    }

    //根据旧密码更新密码
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public
    @ResponseBody
    Response changePassword(String userName, String newPassword, String oldPassword, String roleType) {
        userInfoBaseService.changePassword(userName, newPassword, oldPassword, roleType);
        return new Response("密码修改成功");
    }

    //查所有用户
    @RequestMapping(value = "queryAllUserList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllUserList(String roleType, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime, String keyword, Integer type,Integer internal, Integer sex,
                              Integer page, Integer limit) {
        CutPage<UserInfoBase> cutPage = userInfoBaseService.queryAllUserList(roleType, startTime, endTime, keyword, internal, type,null, sex, null, page, limit);
        UserInfoBase admin = baseRepository.getObjById(UserInfoBase.class, UserContext.getUserId());
        //如果是指定的管理员
        if (admin.isThirdAdmin()) {
            if (startTime == null && endTime == null && type == null && StringUtils.isBlank(keyword)) {
                Integer beforeTotalPage = cutPage.getTotalPage();
                int joinCount = userJoinCount;
                if (sex != null) {
                    switch (sex) {
                        case UserInfoBase.FEMALE:
                            joinCount *= 0.4;
                            break;
                        case UserInfoBase.MALE:
                            joinCount *= 0.56;
                            break;
                        default:
                            joinCount *= 0.01;
                    }
                }
                cutPage.setTotalCount(joinCount + cutPage.getTotalCount());
                Integer laterTotalPage = cutPage.getTotalPage();
                //如果当前页码大于joinPage，获取page - joinPage的用户
                int joinPage = laterTotalPage - beforeTotalPage;
                if (page + 1 > joinPage) {
                    cutPage.setiData(userInfoBaseService.queryAllUserList(roleType, null, null, keyword, internal, null, null, sex, null, page - joinPage, limit).getiData());
                } else if (page + 1 >= beforeTotalPage && page + 1 <= joinPage) {
                    //如果当前页码大于原来总页码，且小于joinPage，使用随机用户
                    //获取一个随机页码
                    Integer randomPage = NumberUtil.getAroundVal((double) beforeTotalPage - 1, 1D, 1).intValue();
                    Integer firstCount = (int) (limit * 0.4);
                    Integer secondCount = (int) (limit * 0.6);
                    List<UserInfoBase> firstData = userInfoBaseService.queryAllUserList(roleType, null, null, keyword, internal, null, null, sex, "update", randomPage, firstCount).getiData();
                    List<UserInfoBase> secondData = userInfoBaseService.queryAllUserList(roleType, null, null, keyword, internal, null, null, sex, "create", randomPage, secondCount).getiData();
                    firstData.addAll(secondData);
                    Collections.shuffle(firstData);
                    cutPage.setiData(firstData);
                }
            }
            cutPage.getiData().forEach(u -> u.setInternalFlag(0));
        }
        return new ObjectResponse<>(cutPage);
    }

    //查询用户相册
    @RequestMapping(value = "getUserAlbum", method = {RequestMethod.POST,RequestMethod.GET})
    public
    @ResponseBody
    Response getUserAlbum(String userId) {
        List<FileUpload> files = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.IMAGE_ALBUM, userId);
        return new ObjectResponse<>(files);
    }


    //根据手机更新密码
    @RequestMapping(value = "changePasswordByPhone", method = RequestMethod.POST)
    public
    @ResponseBody
    Response changePasswordByPhone(String phone, String validCode, String password, String roleType) {
        userInfoBaseService.changePasswordByPhone(phone, validCode, password, roleType);
        return new Response("密码修改成功");
    }

    //根据手机更新支付密码
    @RequestMapping(value = "changePayPasswordByPhone", method = RequestMethod.POST)
    public
    @ResponseBody
    Response changePayPasswordByPhone(String phone, String validCode, String payPassword, String userType) {
        userInfoBaseService.changePayPasswordByPhone(phone, validCode, payPassword, userType);
        return new Response("支付密码修改成功");
    }

    //更新用户融云的token(原Token失效时使用)
    @RequestMapping(value = "updateRongYunToken", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateRongYunToken(String userId, String name, String portraitUri) throws Exception {
        String token = rongYunService.getUserToken(userId, name, portraitUri);
        userInfoBaseService.updateRongYunToken(userId, token);
        String data = "{'token':'" + token + "'}";
        System.out.println("userId:" + userId + "," + data);
        JSONObject jsonObject = JSONObject.fromObject(data);
        return new ObjectResponse<>(jsonObject);
    }

    //--------------------------------------------------------------------------------

    //审核实名认证
    @RequestMapping(value = "updateRealName")
    public
    @ResponseBody
    Response updateRealName(String id, Integer state) throws Exception {

        userInfoBaseService.updateRealName(id, state);
        return new Response("审核完成！");
    }


    //审核账号
    @RequestMapping(value = "changeUserStatus")
    public
    @ResponseBody
    Response changeUserStatus(String userId, Integer status) throws Exception {

        userInfoBaseService.changeUserStatus(userId, status);
        return new Response("修改成功！");
    }

    //----------------------------------------------------------------------------------
    //个人信息

    @RequestMapping(value = "queryMyInfo")
    public
    @ResponseBody
    Response queryMyInfo(String userId) throws Exception {

        UserInfoBase user = userInfoBaseService.getUserInfoById(userId);
        return new ObjectResponse<>(user);
    }

    //个人信息（简化版）
    @RequestMapping(value = "queryMySimpleInfo")
    public
    @ResponseBody
    Response queryMySimpleInfo(String userId) throws Exception {
        UserInfoBase user = userInfoBaseService.getUserSimpleById(userId);
        return new ObjectResponse<>(user);
    }

    @RequestMapping(value = "updateUserInfo")
    public
    @ResponseBody
    Response updateUserInfo(@ModelAttribute UserInfoBase user) throws Exception {
        return new ObjectResponse<>(userInfoBaseService.updateUserInfo(user));
    }

    /**
     * 更新用户个人信息
     */
    @RequestMapping(value = "updateUserPersonalDetail")
    public
    @ResponseBody
    Response updateUserPersonalDetail(UserPersonalDetail userPersonalDetail) throws Exception {
        return new ObjectResponse<>(userPersonalDetailService.updateUserPersonalDetail(userPersonalDetail));
    }

    /**
     * 获取用户个人信息
     */
    @RequestMapping(value = "getUserPersonalDetail")
    public
    @ResponseBody
    Response getUserPersonalDetail(String userId) throws Exception {
        return new ObjectResponse<>(userPersonalDetailService.getUserPersonalDetail(userId));
    }


    //附近的人
    @RequestMapping(value = "queryNearUserForDating")
    public
    @ResponseBody
    Response queryNearUserForDating(Integer page, Integer limit, String x, String y) throws Exception {
        CutPage cutPage = userInfoBaseService.queryNearUserForDating(page, limit, x, y);
        return new ObjectResponse<>(cutPage);
    }

    //附近的人
    @RequestMapping(value = "queryNearUser")
    public
    @ResponseBody
    Response queryNearUser(String userId, Integer sex, Integer page, Integer limit, String x, String y) throws Exception {
        CutPage cutPage = userInfoBaseService.queryNearUser(userId, sex, page, limit, x, y);
        return new ObjectResponse<>(cutPage);
    }
    //更新用户位置信息

    @RequestMapping(value = "updateUserLngAndLat")
    public
    @ResponseBody
    Response updateUserLngAndLat(String userId, String lng, String lat) throws Exception {
        userInfoBaseService.updateUserLngAndLat(userId, lng, lat);
        return new Response("修改成功！");
    }

    @RequestMapping(value = "saveShakeAndGetPerple")
    public
    @ResponseBody
    Response saveShakeAndGetPerple(String fkUserId, String x, String y) throws Exception {
        UserInfoBase user = shakeService.saveShakeAndGetPerple(fkUserId, x, y);
        return new ObjectResponse<>(user);
    }

    @RequestMapping(value = "addSuperManager")
    public
    @ResponseBody
    Response addSuperManager() throws Exception {
        userInfoBaseService.addSuperManager();
        return new Response("添加成功！");
    }

    @RequestMapping(value = "checkLoginWeChat")
    public
    @ResponseBody
    Response checkLoginWeChat(String openId) throws Exception {

        UserInfoBase user = userInfoBaseService.checkLoginWeChat(openId);
        return new ObjectResponse<>(user);
    }

    @RequestMapping(value = "rightWeChat")
    public
    @ResponseBody
    Response rightWeChat(String openId, String headImg,String nickname,String phone, String validCode) {
        tempValidCodeService.checkValidCode(phone, validCode);
        UserInfoBase user = userInfoBaseService.rightWeChat(openId, phone, headImg,nickname);
        return new ObjectResponse<>(user);
    }


    @RequestMapping(value = "realName")
    public
    @ResponseBody
    Response realName(String userId, String realName, String cardNum) {
        UserRealName userRealName = userInfoBaseService.realName(userId, realName, cardNum);
        return new ObjectResponse<>(userRealName);
    }

    @RequestMapping(value = "judgeRealName")
    public
    @ResponseBody
    Response judgeRealName(String userId) {
        Boolean n = userInfoBaseService.judgeRealName(userId);
        return new ObjectResponse<>(n);
    }

    @RequestMapping(value = "queryUserRealName")
    public
    @ResponseBody
    Response queryUserRealName(Integer page, Integer limit) {
        CutPage<UserRealName> cutPage = userInfoBaseService.queryUserRealName(page, limit);
        return new ObjectResponse<>(cutPage);
    }

    @RequestMapping(value = "addAgreement")
    public
    @ResponseBody
    Response addAgreement(String content) {
        Agreement agreement = userInfoBaseService.addAgreement(content);
        return new ObjectResponse<>(agreement);
    }

    @RequestMapping(value = "queryAgreement")
    public
    @ResponseBody
    Response queryAgreement() {
        Agreement agreement = userInfoBaseService.queryAgreement();
        return new ObjectResponse<>(agreement);
    }

    @RequestMapping(value = "querVideoList")
    public
    @ResponseBody
    Response querVideoList(String userId, Integer page, Integer limit) {
        CutPage<UserTeacherVideo> cutPage = userInfoBaseService.querVideoList(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    @RequestMapping(value = "insertJobType")
    public
    @ResponseBody
    Response insertJobType(String name) {
        JobType jobType = userInfoBaseService.insertJobType(name);
        return new ObjectResponse<>(jobType);
    }

    @RequestMapping(value = "updateJobType")
    public
    @ResponseBody
    Response updateJobType(String id, String name) {
        JobType jobType = userInfoBaseService.updateJobType(id, name);
        return new ObjectResponse<>(jobType);
    }

    @RequestMapping(value = "queryJobType")
    public
    @ResponseBody
    Response queryJobType() {
        List<JobType> jobType = userInfoBaseService.queryJobType();
        return new ObjectResponse<>(jobType);
    }

    @RequestMapping(value = "stopJobTye")
    public
    @ResponseBody
    Response stopJobTye(String id, Integer state) {
        JobType jobType = userInfoBaseService.stopJobTye(id, state);
        return new Response("操作成功");
    }

    /**
     * 获取用户的相关信息
     *
     * @param userId
     */
    @RequestMapping(value = "queryUserCorrelative")
    public
    @ResponseBody
    Response queryUserCorrelative(String userId) {
        return new ObjectResponse<>(userCorrelativeService.queryUserCorrelativeByUserId(userId));
    }

    /**
     * 设置推荐码
     *
     * @param userId
     * @param recommendCode
     */
    @RequestMapping(value = "setRecommendCode")
    public
    @ResponseBody
    Response setRecommendCode(String userId, String recommendCode) {
        if (DateUtil.isBetweenPeriod(PropertiesConfig.getRecommendStartDate(), PropertiesConfig.getRecommendEndDate(), new Date())) {
            userRecommendService.addReferrer(userId, recommendCode);
            return new Response("感谢您的使用！");
        }
        return new Response(CommonErrorResult.BUSINESS_ERROR, "不在活动期间！");
    }

    //查所有待审核用户
    @RequestMapping(value = "queryAllUserHeadList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllUserHeadList(String roleType, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime, String keyword, Integer internal, Integer sex,
                                  Integer page, Integer limit) {
        CutPage cutPage = userInfoBaseService.queryAllUserList(roleType, startTime, endTime, keyword, internal, null,UserHeadStatus.AUDIT.getCode(), sex, null, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //添加报名表
    @RequestMapping(value = "addEnrollForm")
    public
    @ResponseBody
    Response addEnrollForm(String userId, String data) throws Exception {
        userInfoBaseService.addEnrollForm(userId, data);
        return new Response("添加成功!");
    }

    //查报名表列表
    @RequestMapping(value = "queryEnrollFormList")
    public
    @ResponseBody
    Response queryEnrollFormList(Integer page, Integer limit) throws Exception {
        CutPage cutPage = userInfoBaseService.queryEnrollFormList(page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //查报名表内容
    @RequestMapping(value = "queryUserEnrollForm")
    public
    @ResponseBody
    Response queryUserEnrollForm(String id) throws Exception {
        EnrollForm enrollForm = userInfoBaseService.queryUserEnrollForm(id);
        return new ObjectResponse<>(enrollForm);
    }

    //提交个人实名认证
    @RequestMapping(value = "addUserAuthentication")
    public
    @ResponseBody
    Response addUserAuthentication(@RequestParam("frontPhoto") MultipartFile[] frontPhoto, @RequestParam("backPhoto") MultipartFile[] backPhoto,
                                   @RequestParam("halfPhoto") MultipartFile[] halfPhoto,
                                   String userId, String name, String cardNum) throws Exception {
        userInfoBaseService.addUserAuthentication(frontPhoto, backPhoto, halfPhoto, userId, name, cardNum);
        return new ObjectResponse<>("提交成功！");
    }

    //查询个人实名验证信息
    @RequestMapping(value = "getUserAuthentication")
    public
    @ResponseBody
    Response getUserAuthentication(String userId) throws Exception {
        return new ObjectResponse<>(userInfoBaseService.getUserAuthentication(userId));
    }

    //查询个人实名认证列表
    @RequestMapping(value = "queryUserAuthenticationList")
    public
    @ResponseBody
    Response queryUserAuthenticationList(Integer status, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(userInfoBaseService.queryUserAuthenticationList(status, page, limit));
    }

    //上传个人视频
    @RequestMapping(value = "addUserVideo")
    public
    @ResponseBody
    Response addUserVideo(String userId, @RequestParam("video") MultipartFile[] video) throws Exception {
        userInfoBaseService.addUserVideo(userId, video);
        return new ObjectResponse<>("已提交！");
    }

    //查询个人视频列表
    @RequestMapping(value = "queryUserVideoList")
    public
    @ResponseBody
    Response queryUserVideoList(Integer status, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(userInfoBaseService.queryUserVideoList(status, page, limit));
    }

    //TODO 即将废弃 申请红人
    @RequestMapping(value = "createInternetStar")
    public
    @ResponseBody
    Response createInternetStar(String userId) throws Exception {
        userInfoBaseService.createAuctionStar(userId, AuctionInternetStarType.INTERNET_STAR);
        return new ObjectResponse<>("已提交申请!");
    }

    //申请网红名人
    @RequestMapping(value = "applyStarCelebrity")
    public
    @ResponseBody
    Response applyStarCelebrity(String userId, Integer type) throws Exception {
        userInfoBaseService.createAuctionStar(userId, AuctionInternetStarType.getByCode(type));
        return new ObjectResponse<>("已提交申请!");
    }

    //查询红人列表
    @RequestMapping(value = "queryInternetStarList")
    public
    @ResponseBody
    Response queryInternetStarList(Integer type, Integer status, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(userInfoBaseService.queryInternetStarList(type, status, page, limit));
    }

    //获取内部会员
    @RequestMapping(value = "queryInternalUser")
    public
    @ResponseBody
    Response queryInternalUser() throws Exception {
        return new ObjectResponse<>(userInfoBaseService.queryUserSimpleInfo());
    }

}
