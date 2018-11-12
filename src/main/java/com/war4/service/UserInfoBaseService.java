package com.war4.service;

import com.war4.base.CutPage;
import com.war4.enums.AuctionInternetStarType;
import com.war4.pojo.*;
import com.war4.vo.SimpleUserVO;
import com.war4.vo.UserSimpleInfoVO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dly on 2016/7/13.
 */
public interface UserInfoBaseService {
    //注册用户-----所有端入口不同，但所有基本信息统一注册
    UserInfoBase insertUserBase(UserInfoBase userInfoBase);

    //根据手机号查询用户
    void findUserInfo(String phone);

    //验证登陆
    UserInfoBase checkLoginParam(String userName, String password, String roleType);

    //根据主键查询用户
    UserInfoBase getUserById(String id);

    //根据主键查询用户详细信息
    UserInfoBase getUserInfoById(String id);

    //获取用户的简单信息
    UserInfoBase getUserSimpleById(String id);
    //获取指定用户群的map
    Map<String, UserInfoBase> queryUserMap(Collection<String> ids);

    /**
     * 获取指定的用户简单信息
     * @param ids
     * @param lng   经度
     * @param lat   纬度
     */
    Map<String, SimpleUserVO> querySimpleUserMap(Collection<String> ids,String lng,String lat);
    Map<String, SimpleUserVO> querySimpleUserMap(Collection<String> ids);

    UserInfoBase getUserInfoByOpenId(String code);

    UserInfoBase checkLoginWeChat(String openId);

    UserInfoBase rightWeChat(String openId, String phone, String headImg, String nickname);

    //修改个人信息
    UserInfoBase updateUserInfo(UserInfoBase userInfoBase) throws Exception;

    //修改密码
    void updateUserPassword(String userId, String password);

    //根据旧密码更新密码
    void changePassword(String userName, String newPassword, String oldPassword, String roleType);

    //根据手机更新密码
    void changePasswordByPhone(String phone, String validCode, String password, String userType);

    //审核用户账号
    void changeUserStatus(String userId, Integer status);

    /**
     * 更新用户经纬度
     *
     * @param lng 经度
     * @param lat 纬度
     */
    void updateUserLngAndLat(String userId, String lng, String lat);

    CutPage<UserInfoBase> queryAllUserList(String roleType, Date startTime, Date endTime, String keyword, Integer internal,Integer type,
                                           Integer headState, Integer sex, String orderBy, Integer page, Integer limit);

    void addSuperManager() throws Exception;

    CutPage<UserInfoBase> queryNearUserForDating(Integer page, Integer limit, String x, String y);

    CutPage<UserInfoBase> queryNearUser(String userId, Integer sex, Integer page, Integer limit, String x, String y);

    //根据手机更新支付密码
    void changePayPasswordByPhone(String phone, String validCode, String payPassword, String userType);

    void checkPayPassword(String userId, String password);
    UserInfoBase getUserByPhoneAndRoleType(String phone, String roleType);
    //实名认证
    UserRealName realName(String userId, String realName, String cardNum);

    //判断是否实名认证过
    Boolean judgeRealName(String userId);

    //查实名认证列表
    CutPage<UserRealName> queryUserRealName(Integer page, Integer limit);

    //审核实名认证
    void updateRealName(String id, Integer state);

    //设置主播协议
    Agreement addAgreement(String content);

    //查主播协议
    Agreement queryAgreement();

    //查名师直播
    CutPage<UserTeacherVideo> querVideoList(String userId, Integer page, Integer limit);

    //添加职业
    JobType insertJobType(String name);

    //修改职业
    JobType updateJobType(String id, String name);

    //查职业列表
    List<JobType> queryJobType();

    //停用/启用职业
    JobType stopJobTye(String id, Integer state);

    //更新融云Token
    void updateRongYunToken(String userId, String token);

    /**
     * 根据推荐码获取用户
     *
     * @param recommendCode
     */
    UserInfoBase getUserByRecommendCode(String recommendCode);

    /**
     * 获取用户分享相关信息
     */
    UserInfoBase getUserShareDetail(String userId);

    /**
     * 编辑头像状态
     */
    void updateUserHeadState(String userId, Integer state);

    /**
     * 添加会员报名表
     */
    void addEnrollForm(String userId, String data);

    //查会员报名表列表
    CutPage<EnrollForm> queryEnrollFormList(Integer page, Integer limit);

    //查会员报名表内容
    EnrollForm queryUserEnrollForm(String id);

    //添加新用户注册
    NewUserRegister createUserRegister(String userId);

    //查询新注册用户
    CutPage<NewUserRegister> queryUserRegister(Byte state,Date endTime,Integer page,Integer limit);

    //更新新注册用户状态已完成
    NewUserRegister updateUserRegisterDone(Long id,String remark);

    void addUserVideo(String userId,@RequestParam("video")MultipartFile[] video);

    CutPage<UserAuthentication>queryUserAuthenticationList(Integer status,Integer page,Integer limit);
    CutPage<UserVideo>queryUserVideoList(Integer status,Integer page,Integer limit);

    CutPage<InternetStar>queryInternetStarList(Integer type, Integer status, Integer page, Integer limit);

    void createAuctionStar(String userId, AuctionInternetStarType type);

    void addUserAuthentication(@RequestParam("frontPhoto")MultipartFile[] frontPhoto, @RequestParam("backPhoto")MultipartFile[] backPhoto,
                               @RequestParam("halfPhoto")MultipartFile[] halfPhoto,
                               String userId, String name, String cardNum);

    UserAuthentication getUserAuthentication(String userId);

    List<UserSimpleInfoVO> queryUserSimpleInfo();

    List<UserInfoBase> queryUserByBitState(Long... bitState);
}
