package com.war4.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.*;
import com.war4.listener.event.UserRegisterEvent;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.*;
import com.war4.vo.SimpleUserVO;
import com.war4.vo.UserSimpleInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dly on 2016/7/12.
 */
@Service
public class UserInfoBaseServiceImpl implements UserInfoBaseService, ApplicationContextAware {

    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private AssembleService assembleService;
    @Autowired
    private TempValidCodeService tempValidCodeService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserVIPService userVIPService;

    @Autowired
    private UserGiftService userGiftService;

    @Autowired
    private AnchorFanService anchorFanService;

    @Autowired
    private UserAccessTokenService userAccessTokenService;

    @Autowired
    private DatingSettingService datingSettingService;

    @Autowired
    private UserCorrelativeService userCorrelativeService;

    @Autowired
    private UserPersonalDetailService userPersonalDetailService;

    @Autowired
    private RongYunService rongYunService;

    @Autowired
    private FileService fileService;

    private ApplicationContext ac;

    @Override
    @Transactional
    public UserInfoBase getUserById(String id) {

        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, id);

        return assembleService.assembleObject(user);
    }


    @Override
    @Transactional
    public UserInfoBase getUserInfoById(String id) {

        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, id);
        if (user != null) {
            user.setFanSum(anchorFanService.queryMyFan(id, 0, CutPage.MAX_COUNT).getTotalCount().intValue());
            user.setGiftSum(userGiftService.queryMyGetGift(id, 0, CutPage.MAX_COUNT).getTotalCount().intValue());
            user = assembleService.assembleObject(user);
        }

        return user;
    }

    @Override
    public UserInfoBase getUserSimpleById(String id) {
        return baseRepository.getObjById(UserInfoBase.class, id);
    }

    @Override
    public Map<String, UserInfoBase> queryUserMap(Collection<String> ids) {
        if (ids.size() <= 0) {
            return new HashMap<>();
        }
        Map<String, Object> paramMap = baseRepository.paramMap();
        String hql = "from UserInfoBase where id in :ids";
        paramMap.put("ids",ids);
        List<UserInfoBase> result = baseRepository.queryHqlResult(hql, paramMap, 0, ids.size());
        return result.stream().collect(Collectors.toMap(UserInfoBase::getId, u -> u));
    }

    @Override
    public Map<String, SimpleUserVO> querySimpleUserMap(Collection<String> ids, String lng, String lat) {
        if (ids.size() <= 0) {
            return new HashMap<>();
        }
        String sql = "SELECT id userId,IFNULL(TIMESTAMPDIFF(YEAR,birth,CURDATE()),0) age,sex,nick_name nickname," +
                "user_photo_head headImg,ST_DISTANCE(point,point(:lng,:lat)) * 111195 distance FROM user_info_base WHERE id IN :ids";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("ids",ids);
        paramMap.put("lng",lng);
        paramMap.put("lat",lat);
        List<SimpleUserVO> result = baseRepository.querySqlResult(sql, paramMap, SimpleUserVO.class, 0, CutPage.MAX_COUNT);
        return result.stream().collect(Collectors.toMap(SimpleUserVO::getUserId, u -> u));
    }

    @Override
    public Map<String, SimpleUserVO> querySimpleUserMap(Collection<String> ids) {
        return this.querySimpleUserMap(ids,null, null);
    }

    @Override
    public UserInfoBase getUserInfoByOpenId(String weOpenId) {
        String hql = baseRepository.getBaseHqlByClass(UserInfoBase.class);
        hql += " and weOpenId ='" + weOpenId + "' ";
        return baseRepository.queryUniqueData(hql);

    }


    /*
        第三方登录思路
        目前只有一个微信登录所以我偷懒直接把openid放在user表里面了
        这里合理的设计应该是分表，把第三方登录获取的信息放在分表里
        在这里第三方登录我的设计思路是
            因为第三方获取的openid本来就是唯一的，所以我在登录的时候判断
        表内是否有这个openid,如果是已经拥有的openid，那就可以判断是同一
        个人返回该用户的信息。
            如果这个openid是没有，那久按照注册一个用户的方式进行处理并且
        初始化一些和用户相关的数据。


     */
    @Override
    @Transactional
    public UserInfoBase checkLoginWeChat(String openId) {
        UserInfoBase userInfoBase = getUserInfoByOpenId(openId);
        if (userInfoBase == null) {
            throw new BusinessException(CommonErrorResult.WECHAT_ERROR, "请完善个人信息");
        }
        return assembleService.assembleObject(userInfoBase);


    }

    @Override
    @Transactional
    public UserInfoBase rightWeChat(String openId, String phone, String headImg, String nickname) {
        if (StringUtils.isBlank(openId)) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "openId为null！");
        }

        if (getUserInfoByOpenId(openId) != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该用户已经存在！");
        }
        UserInfoBase userBase = getUserByPhoneAndRoleType(phone, UserRoleType.COMMON_USER.getCode());
        if (userBase != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "手机号已经注册了！");
        }
        //重复用户名注册
        UserInfoBase oldUsername = getUserByUsernameAndRoleType(phone, UserRoleType.COMMON_USER.getCode());
        if (oldUsername != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "重复的用户名注册");
        }

        UserInfoBase user = new UserInfoBase();
        user.setId(OrderUtil.getUUID());
        user.setUsername(phone);
        user.setPassword(null);
        user.setPhone(phone);
        if (StringUtils.isNoneBlank(nickname)) {
            user.setNickName(nickname);
        }else {
            user.setNickName("微信用户" + user.getId().substring(0, 10));
        }
        user.setUserPhotoHead(headImg);
        user.setRoleType(UserRoleType.COMMON_USER.getCode());
        user.setHeadState(UserHeadStatus.FAILED.getCode());
        user.setRecommendCode(ShareCodeUtil.getShareCode());
        user.setWeOpenId(openId);
        //初始化状态

        user.setStatus(UserStatus.NORMAL.getCode());

        baseRepository.saveObj(user);

        initializeUser(user.getId());

        assembleService.assembleObject(user);
        //发送用户注册事件
        ac.publishEvent(new UserRegisterEvent(user));
        return user;
    }

    @Override
    @Transactional
    public UserInfoBase updateUserInfo(UserInfoBase userInfoBase) throws Exception {
        if (StringUtils.isBlank(userInfoBase.getId())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该用户Id不能为空");
        }
        if (StringUtils.isBlank(userInfoBase.getUserPhotoHead())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该用户头像不能为空");
        }
        if (StringUtils.isBlank(userInfoBase.getNickName())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该用户昵称不能为空");
        }
        /*
        if (userInfoBase.getRole() != null) {
            if (UserRole.getByCode(userInfoBase.getRole()) == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不存在的角色类型");
            }
        }
        */
        UserInfoBase seleUser = getUserSimpleById(userInfoBase.getId());
        if (seleUser == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该用户未启用");
        }

        //注册融云token
        if (!seleUser.getNickName().equals(userInfoBase.getNickName()) || !userInfoBase.getUserPhotoHead().equals(seleUser.getUserPhotoHead())) {
            //当两个信息中的一个改变时才更新token
            seleUser.setToken(rongYunService.getUserToken(seleUser.getId(), userInfoBase.getNickName(), userInfoBase.getUserPhotoHead()));
        }
        seleUser.setNickName(userInfoBase.getNickName());
        seleUser.setUserPhotoHead(userInfoBase.getUserPhotoHead());
        if (userInfoBase.getIntro() != null) {
            seleUser.setIntro(userInfoBase.getIntro());
        }
        if (seleUser.getSex().equals(-1)) {//没有选择性别才能修改
            seleUser.setSex(userInfoBase.getSex());
        }
        if (userInfoBase.getRole() != null) {
            seleUser.setRole(userInfoBase.getRole());
        }
        if (userInfoBase.getBirth() != null) {
            seleUser.setBirth(userInfoBase.getBirth());
            seleUser.setAge(DateUtil.getAgeByBirth(seleUser.getBirth()));
            seleUser.setConstellation(DateUtil.getAstro(seleUser.getBirth()));
        }
        Integer headState = userInfoBase.getHeadState();
        UserHeadStatus userHeadStatus = UserHeadStatus.getByCode(headState);
        if (headState != null && userHeadStatus != null //状态码存在
                && !headState.equals(UserHeadStatus.PASS.getCode())//不能自己填通过
                && !seleUser.getInternalFlag().equals(1)) {//不是内部会员
            seleUser.setHeadState(headState);
        }
        baseRepository.updateObj(seleUser);
        return seleUser;
    }

    @Override
    @Transactional
    public void updateUserPassword(String userId, String password) {
        UserInfoBase userInfoBase = getUserById(userId);
        if (userInfoBase == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        userInfoBase.setPassword(MD5Util.encode(password));
        baseRepository.updateObj(userInfoBase);
    }

    @Override
    @Transactional
    public UserInfoBase insertUserBase(UserInfoBase userInfoBase) {
        //指定用户类型不存在
        if (UserRoleType.getByValue(userInfoBase.getRoleType()) == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "用户类型不存在");
        }
        if (!StringUtils.isNotEmpty(userInfoBase.getPassword())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "密码不能为空");
        }
        if (userInfoBase.getUsername() == null) {
            userInfoBase.setUsername(userInfoBase.getPhone());
        }
        if (userInfoBase.getNickName() == null) {
            userInfoBase.setNickName(userInfoBase.getUsername());
        }
        UserInfoBase userBase = getUserByPhoneAndRoleType(userInfoBase.getPhone(), userInfoBase.getRoleType());
        if (userBase != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "手机号已经注册了！");
        }
        //重复用户名注册
        UserInfoBase oldUsername = getUserByUsernameAndRoleType(userInfoBase.getUsername(), userInfoBase.getRoleType());
        if (oldUsername != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "重复的用户名注册");
        }

        UserInfoBase user = new UserInfoBase();
        user.setId(OrderUtil.getUUID());
        user.setUsername(userInfoBase.getUsername());
        user.setPassword(MD5Util.encode(userInfoBase.getPassword()));
        user.setPhone(userInfoBase.getPhone());
        user.setNickName(userInfoBase.getNickName());
        user.setSex(userInfoBase.getSex());
        user.setIntro(userInfoBase.getIntro());
        user.setBirth(userInfoBase.getBirth());
        user.setAge(DateUtil.getAgeByBirth(user.getBirth()));
        user.setConstellation(DateUtil.getAstro(user.getBirth()));
        user.setX(userInfoBase.getX());
        user.setY(userInfoBase.getY());
        /*if (StringUtils.isBlank(user.getUserPhotoHead())) {
            user.setUserPhotoHead(PropertiesConifig.getDefaultUserPhotoHead());
        }*/
        user.setHeadState(UserHeadStatus.FAILED.getCode());
        user.setRoleType(userInfoBase.getRoleType());
        user.setRecommendCode(ShareCodeUtil.getShareCode());
        //初始化状态
        user.setStatus(UserStatus.NORMAL.getCode());

        baseRepository.saveObj(user);

        this.initializeUser(user.getId());

        //普通用户发送用户注册事件
        if (UserRoleType.COMMON_USER.getCode().equals(userInfoBase.getRoleType())) {
            ac.publishEvent(new UserRegisterEvent(user));
        }
        return user;
    }


    //验证登陆
    @Override
    public UserInfoBase checkLoginParam(String userName, String password, String roleType) {
        UserInfoBase dbUser = getUserByUsernameAndRoleType(userName, roleType);
        if (dbUser == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (StringUtils.isBlank(password)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户密码不能为空！");
        }

        String md5Password = MD5Util.encode(password);
        if (md5Password != null && md5Password.equals(dbUser.getPassword()) && roleType.equals(dbUser.getRoleType())) {
            if (dbUser.getStatus().equals(UserStatus.PENDING.getCode())) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您的账号正在审核");
            }
            if (dbUser.getStatus().equals(UserStatus.STOP.getCode())) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您的账号已停用");
            }
            //更新accessToken
            userAccessTokenService.refreshAccessToken(dbUser.getId());
            dbUser = assembleService.assembleObject(dbUser);
            return dbUser;
        } else return null;
    }

    //根据旧密码更新密码
    @Override
    @Transactional
    public void changePassword(String userName, String newPassword, String oldPassword, String roleType) {
        if (userName == null || newPassword == null || oldPassword == null || roleType == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数不全");
        }
        UserInfoBase user = getUserByUsernameAndRoleType(userName, roleType);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (!MD5Util.encode(oldPassword).equals(user.getPassword())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "旧密码不正确");
        }
        user.setPassword(MD5Util.encode(newPassword));
        baseRepository.updateObj(user);
        userAccessTokenService.refreshAccessToken(user.getId());
    }

    @Override
    @Transactional
    //根据手机更新密码
    public void changePasswordByPhone(String phone, String validCode, String password, String userType) {
        UserInfoBase userBase = getUserByPhoneAndRoleType(phone, userType);
        if (userBase == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        tempValidCodeService.checkValidCode(phone, validCode);
        if (!StringUtils.isNotEmpty(password)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "密码不能为空");
        }
        userBase.setPassword(MD5Util.encode(password));
        baseRepository.updateObj(userBase);
        userAccessTokenService.refreshAccessToken(userBase.getId());
    }

    @Override
    @Transactional
    public void changeUserStatus(String userId, Integer status) {
        if (status == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "要设置用户状态不能为空");
        }
        if (UserStatus.getByCode(status) == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户状态不存在");
        }
        UserInfoBase userInfoBase = getUserById(userId);
        if (userInfoBase == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        userInfoBase.setStatus(status);
        baseRepository.updateObj(userInfoBase);
    }

    @Override
    @Transactional
    public void updateUserLngAndLat(String userId, String lng, String lat) {
        String sql = "UPDATE user_info_base SET x = :lng,y = :lat, point = POINT(:lng,:lat),update_time = :updateTime WHERE id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("updateTime", new Date());
        params.put("lng", lng);
        params.put("lat", lat);
        baseRepository.executeSql(sql, params, null);
    }

    @Override
    public CutPage<UserInfoBase> queryAllUserList(String roleType, Date startTime, Date endTime, String keyword, Integer internal,Integer type,
                                                  Integer headState, Integer sex, String orderBy, Integer page, Integer limit) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("from UserInfoBase where delFlag = 0");
        Map<String, Object> param = new HashMap<>();
        if (roleType != null) {
            if (UserRoleType.getByValue(roleType) == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户类型不存在");
            }
            sb.append(" and roleType = :roleType ");
            param.put("roleType", roleType);
        }
        if (StringUtils.isNotBlank(keyword)) {
            sb.append(" and (id like :keyword or username like :keyword or phone like :keyword or nickName like :keyword) ");
            param.put("keyword", "%" + keyword + "%");
        }
        if (startTime != null) {
            sb.append(" and createTime >= :startTime");
            param.put("startTime", startTime);
        }
        if (endTime != null) {
            sb.append(" and createTime <= :endTime");
            param.put("endTime", DateUtil.endOfDay(endTime));
        }
        if (internal != null) {
            sb.append(" and internalFlag = :internal");
            param.put("internal", internal);
        }
        if (type != null) {
            switch (type){
                case 0 :
                    sb.append(" and internalFlag = :internal ");
                    param.put("internal", 0);
                    break;
                case 1 :
                    sb.append(" and internalFlag = :internal ");
                    param.put("internal", 1);
                    break;
                case 2 ://查网红
                    sb.append(" and internalFlag = :internal and bitand(bitState,:value) != 0");
                    param.put("internal", 0);
                    param.put("value", BitStateUtil.OP_HAS_INTERNETSTAR_PASS);
                    break;
                case 3 ://查名人
                    sb.append(" and internalFlag = :internal and bitand(bitState,:value) != 0");
                    param.put("internal", 0);
                    param.put("value", BitStateUtil.OP_HAS_CELEBRITY_PASS);
                    break;
            }
        }
        if (sex != null) {
            sb.append(" and sex = :sex");
            param.put("sex", sex);
        }
        if (headState != null) {
            sb.append(" and headState = :headState");
            param.put("headState", headState);
        }
        if (StringUtils.isNotBlank(orderBy)) {
            switch (orderBy) {
                case "update":
                    sb.append(" order by updateTime");
                    break;
                case "create":
                    sb.append(" order by createTime");
                    break;
            }
        }else {
            sb.append(" order by updateTime desc");
        }
        return baseRepository.executeHql(sb, param, page, limit);
    }

    @Override
    @Transactional
    public void addSuperManager() throws Exception {
        UserInfoBase user = new UserInfoBase();
        user.setRoleType(UserRoleType.SUPERMANAGER.getCode());
        user.setUsername(PropertiesConfig.getSuperManagerAccount());
        user.setPassword(PropertiesConfig.getSuperManagerPassword());
        user.setNickName(PropertiesConfig.getSuperManagerNickName());
        insertUserBase(user);
    }

    @Override
    public CutPage<UserInfoBase> queryNearUserForDating(Integer page, Integer limit, String x, String y) {
        StringBuffer stringBuffer = new StringBuffer(baseRepository.getBaseHqlByClass(UserInfoBase.class));
        stringBuffer.append(" and x is not null and y is not null ");
        stringBuffer.append(" and roleType='").append(UserRoleType.COMMON_USER.getCode());
        stringBuffer.append("' order by ");
        stringBuffer.append("(POWER(MOD(ABS(x - ").append(x).append("),360),2) + POWER(ABS(y - ").append(y).append("),2))");


        CutPage<UserInfoBase> cutPage = new CutPage(page, limit);
        cutPage = baseRepository.queryCutPageData(stringBuffer.toString(), cutPage);
        for (UserInfoBase userInfoBase : cutPage.getiData()) {
            assembleService.assembleObject(userInfoBase);
        }
        return cutPage;
    }

    @Override
    public CutPage<UserInfoBase> queryNearUser(String userId, Integer sex, Integer page, Integer limit, String x, String y) {
        StringBuffer stringBuffer = new StringBuffer(baseRepository.getBaseHqlByClass(UserInfoBase.class));
        stringBuffer.append(" and x is not null and y is not null ");
        if (sex != null) {
            stringBuffer.append(" and sex = " + sex + " ");
        }
        if (userId != null) {
            stringBuffer.append(" and id != '" + userId + "' ");
        }
        stringBuffer.append(" and roleType='").append(UserRoleType.COMMON_USER.getCode());
        stringBuffer.append("' order by ");
        stringBuffer.append("(POWER(MOD(ABS(x - ").append(x).append("),360),2) + POWER(ABS(y - ").append(y).append("),2))");


        CutPage<UserInfoBase> cutPage = new CutPage(page, limit);
        cutPage = baseRepository.queryCutPageData(stringBuffer.toString(), cutPage);
        for (UserInfoBase userInfoBase : cutPage.getiData()) {
            assembleService.assembleObject(userInfoBase);
        }
        return cutPage;
    }

    @Override
    @Transactional
    public void changePayPasswordByPhone(String phone, String validCode, String password, String userType) {
        UserInfoBase userBase = getUserByPhoneAndRoleType(phone, userType);
        if (userBase == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (!NumberUtil.isNumeric(password)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "支付密码只能为数字");
        }
        if (password.length() != 6) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "支付密码必须为6位");
        }
        tempValidCodeService.checkValidCode(phone, validCode);
        userBase.setPayPassword(MD5Util.encode(password));
        userBase.addState(BitStateUtil.OP_HAS_PAY_PASSWORD);
        baseRepository.updateObj(userBase);
    }

    @Override
    public void checkPayPassword(String userId, String payPassword) {
        if (StringUtils.isBlank(payPassword)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "支付密码不能为空");
        }
        UserInfoBase userInfoBase = getUserById(userId);
        if (userInfoBase == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (StringUtils.isBlank(userInfoBase.getPayPassword())) {
            throw new BusinessException(CommonErrorResult.LACK_PAY_PASSWORD, "用户还没有设置密码");
        }
        String md5Password = MD5Util.encode(payPassword);
        if (!userInfoBase.getPayPassword().equals(md5Password)) {
            throw new BusinessException(CommonErrorResult.LACK_PAY_PASSWORD, "支付密码错误");
        }
    }


    private UserInfoBase getUserByUsernameAndRoleType(String username, String roleType) {
        String hql = baseRepository.getBaseHqlByClass(UserInfoBase.class);
        hql += "and username='" + username + "' and roleType='" + roleType + "'";
        return baseRepository.queryUniqueData(hql);
    }

    public UserInfoBase getUserByPhoneAndRoleType(String phone, String roleType) {
        String hql = baseRepository.getBaseHqlByClass(UserInfoBase.class);
        hql += "and phone='" + phone + "' and roleType='" + roleType + "'";
        return baseRepository.queryUniqueData(hql);
    }

    /*------------主播协议，实名认证--------------*/
    @Override
    @Transactional
    public UserRealName realName(String userId, String realName, String cardNum) {

        UserRealName userRealName = new UserRealName();
        userRealName.setId(userId);
        userRealName.setRealName(realName);
        userRealName.setProvinceCard(cardNum);
        baseRepository.saveObj(userRealName);
        return userRealName;
    }

    @Override
    public Boolean judgeRealName(String userId) {
        UserRealName userRealName = baseRepository.getObjById(UserRealName.class, userId);
        if (userRealName == null) {
            throw new BusinessException(CommonErrorResult.REEA, "没有实名认证");
        }
        if (userRealName.getState() == 1) {
            return true;
        } else {
            if (userRealName.getState() == 0) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "等待审核中");
            }
            if (userRealName.getState() == 2) {
                throw new BusinessException(CommonErrorResult.WECHAT_ERROR, "审核没通过，请联系平台");
            }
            return false;
        }

    }

    @Override
    @Transactional
    public void updateRealName(String id, Integer state) {
        UserRealName userRealName = baseRepository.getObjById(UserRealName.class, id);
        userRealName.setState(state);
        baseRepository.updateObj(userRealName);
    }

    @Override
    public CutPage<UserRealName> queryUserRealName(Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(UserRealName.class);
        CutPage<UserRealName> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql, cutPage);
        for (UserRealName userRealName : cutPage.getiData()) {
            UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, userRealName.getId());
            assembleService.assembleObject(userInfoBase);
            userRealName.setUserInfoBase(userInfoBase);
            assembleService.assembleObject(userRealName);
        }
        return cutPage;
    }

    @Override
    @Transactional
    public Agreement addAgreement(String content) {
        Agreement agreement = new Agreement();
        agreement.setId(OrderUtil.getUUID());
        agreement.setContent(content);
        baseRepository.saveObj(agreement);
        return agreement;
    }

    @Override
    public void findUserInfo(String phone) {
        String hql = baseRepository.getBaseHqlByClass(UserInfoBase.class);
        hql += "and phone='" + phone + "'";
        UserInfoBase userInfoBase = baseRepository.queryUniqueData(hql);
        if (userInfoBase != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "此号码已注册");
        }
    }

    @Override
    public Agreement queryAgreement() {
        String hql = baseRepository.getBaseHqlByClass(Agreement.class);
        hql += "order by createTime desc";
        Agreement agreement = baseRepository.queryUniqueData(hql);
        if (agreement != null) {
            return agreement;
        } else {
            return addAgreement("");
        }
    }

    @Override
    @Transactional
    public CutPage<UserTeacherVideo> querVideoList(String userId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(UserTeacherVideo.class);
        hql += "and userId= '" + userId + "'";
        CutPage<UserTeacherVideo> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql, cutPage);
        for (UserTeacherVideo userTeacherVideo : cutPage.getiData()) {
            Long timestamp = userTeacherVideo.getCreateTime().getTime();
            Long nowTime = new Date().getTime();
            if (nowTime - timestamp > 7 * 24 * 60 * 60 * 1000L) {
                baseRepository.logicDelete(userTeacherVideo);
            }
        }
        String hqls = baseRepository.getBaseHqlByClass(UserTeacherVideo.class);
        hqls += "and userId= '" + userId + "'";
        CutPage<UserTeacherVideo> cutPages = new CutPage<>(page, limit);
        cutPages = baseRepository.queryCutPageData(hqls, cutPages);
        return cutPages;
    }

    @Override
    @Transactional
    public JobType insertJobType(String name) {
        JobType jobType = new JobType();
        jobType.setId(OrderUtil.getUUID());
        jobType.setName(name);
        baseRepository.saveObj(jobType);
        return jobType;
    }

    @Override
    @Transactional
    public JobType updateJobType(String id, String name) {
        JobType jobType = baseRepository.getObjById(JobType.class, id);
        jobType.setName(name);
        baseRepository.updateObj(jobType);
        return jobType;
    }

    @Override
    public List<JobType> queryJobType() {
        String hql = baseRepository.getBaseHqlByClass(JobType.class);
        hql += "and state=0";
        CutPage<JobType> cutPage = new CutPage<>(0, 1000);
        cutPage = baseRepository.queryCutPageData(hql, cutPage);
        return cutPage.getiData();
    }

    @Override
    @Transactional
    public JobType stopJobTye(String id, Integer state) {
        JobType jobType = baseRepository.getObjById(JobType.class, id);
        jobType.setState(state);
        baseRepository.updateObj(jobType);
        return jobType;
    }

    @Override
    public void updateRongYunToken(String userId, String token) {
        //获取用户
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user != null && StringUtils.isNotEmpty(token)) {
            //更新融云帐号
            user.setToken(token);
            baseRepository.updateObj(user);
        }
    }

    @Override
    public UserInfoBase getUserByRecommendCode(String recommendCode) {
        String hql = "from UserInfoBase where recommendCode = :recommendCode";
        Map<String, Object> param = new HashMap<>();
        param.put("recommendCode", recommendCode);
        return baseRepository.executeHql(hql, param);
    }

    @Override
    public UserInfoBase getUserShareDetail(String userId) {
        String hql = "select new UserInfoBase(nickName,recommendCode,sex,userPhotoHead) from UserInfoBase where delFlag = 0 and id = :userId";
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return baseRepository.executeHql(hql, param);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }


    @Override
    @Transactional
    public void updateUserHeadState(String userId, Integer state) {
        UserInfoBase userInfoBase = getUserById(userId);
        if (userInfoBase == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        UserHeadStatus headStatus = UserHeadStatus.getByCode(state);
        if (headStatus == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"头像状态异常");
        }
        userInfoBase.setHeadState(headStatus.getCode());
        baseRepository.updateObj(userInfoBase);
    }

    //初始化用户信息
    private void initializeUser(String userId) {
        userAccountService.addUserAccount(userId);

        userVIPService.saveUserVIP(userId, "0", 0);

        userCorrelativeService.createUserCorrelative(userId);

        userPersonalDetailService.createUserPersonalDetail(userId);

        datingSettingService.addDatingSetting(userId, DatingSettingIsAccept.ACCEPT.getCode(), null);
        //用户accessToken---新增
        userAccessTokenService.insertAccessToken(userId);
    }

    @Override
    @Transactional
    public void addEnrollForm(String userId, String data) {
        UserInfoBase userInfoBase = getUserById(userId);
        if (userInfoBase == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        CutPage<EnrollForm> enrollFormCutPage = baseRepository.executeHql("from EnrollForm where userId = :userId", map, 0, Integer.MAX_VALUE);
        if (enrollFormCutPage.getiData().size() != 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能重复提交报名表！");
        }

        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray name = jsonObject.getJSONArray("Q1");
        JSONArray sex = jsonObject.getJSONArray("Q2");
        JSONArray birth = jsonObject.getJSONArray("Q3");
        JSONArray height = jsonObject.getJSONArray("Q4");
        JSONArray weight = jsonObject.getJSONArray("Q5");
        JSONArray phone = jsonObject.getJSONArray("Q12");
        JSONArray email = jsonObject.getJSONArray("Q15");
        JSONArray idCard = jsonObject.getJSONArray("Q11");
        JSONArray albumFlag = jsonObject.getJSONArray("Q29");

        String name_str = jsonArrayToString(name);
        String sex_str = jsonArrayToString(sex);
        String birth_str = jsonArrayToString(birth);
        String height_str = jsonArrayToString(height);
        String weight_str = jsonArrayToString(weight);
        String phone_str = jsonArrayToString(phone);
        String email_str = "";
        if (email != null) {
            email_str = jsonArrayToString(email);
        }
        String idCard_str = jsonArrayToString(idCard);
        String albumFlag_str = jsonArrayToString(albumFlag);

        EnrollForm enrollForm = new EnrollForm();
        enrollForm.setId(UUID.randomUUID().toString());
        enrollForm.setName(name_str);
        enrollForm.setSex(sex_str);
        enrollForm.setBirth(birth_str);
        enrollForm.setHeight(height_str);
        enrollForm.setWeight(weight_str);
        if (email != null) {
            enrollForm.setEmail(email_str);
        }
        enrollForm.setPhone(phone_str);
        enrollForm.setIdCard(idCard_str);
        enrollForm.setAnwser(data);
        enrollForm.setUserId(userId);
        enrollForm.setAlbumFlag(albumFlag_str);
        enrollForm.setCreateTime(new Date());
        baseRepository.saveObj(enrollForm);

    }

    @Override
    public CutPage<EnrollForm> queryEnrollFormList(Integer page, Integer limit) {

        String hql = "from EnrollForm order by createTime desc";
        return baseRepository.executeHql(hql, null, page, limit);
    }

    @Override
    public EnrollForm queryUserEnrollForm(String id) {
        EnrollForm enrollForm = baseRepository.getObjById(EnrollForm.class, id);
        if (enrollForm == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "报名表id不存在");
        }
        assembleService.assembleObject(enrollForm);

        return enrollForm;
    }

    @Override
    @Transactional
    public NewUserRegister createUserRegister(String userId) {
        NewUserRegister nur = new NewUserRegister(userId);
        baseRepository.saveObj(nur);
        return nur;
    }

    @Override
    public CutPage<NewUserRegister> queryUserRegister(Byte state, Date endTime, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        Map<String, Object> map = new HashMap<>();
        hql.append("from NewUserRegister where state = :state and created <= :endTime");
        map.put("state", state);
        map.put("endTime", endTime);
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public NewUserRegister updateUserRegisterDone(Long id, String remark) {
        NewUserRegister nur = baseRepository.getObjById(NewUserRegister.class, id);
        nur.setState(NewUserRegister.DONE);
        nur.setRemark(remark);
        nur.setUpdated(new Date());
        baseRepository.updateObj(nur);
        return nur;
    }

    @Override
    @Transactional
    public void addUserVideo(String userId, @RequestParam("video") MultipartFile[] video) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (user.getHasVideoUpload()) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您已提交视频审核");
        }
        if (user.getHasVideoChatPass()) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您已通过视频聊天");
        }
        try {
            UserVideo userVideo = new UserVideo();
            userVideo.setApplierId(userId);
            userVideo.setVideo(fileService.saveFile(FileTypeAndPath.VIDEO_TYPE_PATH, FilePurpose.USER_VIDEO, userId, video).get(0));
            userVideo.setStatus(AuditStatus.CREATE.getCode());
            baseRepository.saveObj(userVideo);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "视频上传异常，请联系客服！");
        }
        user.addState(BitStateUtil.OP_HAS_VIDEO_UPLOAD);
        user.addState(BitStateUtil.OP_HAS_VIDEO_CHAT_REVIEW);
        baseRepository.updateObj(user);
    }

    @Override
    public CutPage<UserAuthentication> queryUserAuthenticationList(Integer status, Integer page, Integer limit) {

        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();
        hql.append("from UserAuthentication where 1=1 ");

        if (status != null && status != 0) {
            hql.append("and status = :status ");
            map.put("status", status);
        }
        hql.append("order by createTime desc ");
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public CutPage<UserVideo> queryUserVideoList(Integer status, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();
        hql.append("from UserVideo where ");

        if (status != null && status != 0) {
            hql.append(" status = :status ");
            map.put("status", status);
        } else {
            hql.append(" status != :status ");
            map.put("status", AuditStatus.AWAIT.getCode());
        }

        hql.append("order by createTime desc ");
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public CutPage<InternetStar> queryInternetStarList(Integer type, Integer status, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> paramMap = baseRepository.paramMap();
        hql.append("from InternetStar where type = :type ");
        paramMap.put("type", type);
        if (status != null && status != 0) {
            hql.append("and status = :status ");
            paramMap.put("status", status);
        }
        hql.append("order by createTime desc ");
        CutPage<InternetStar> internetStarCutPage = baseRepository.executeHql(hql, paramMap, page, limit);
        for (InternetStar internetStar : internetStarCutPage.getiData()) {
            internetStar.setUserVideo(baseRepository.getObjById(UserVideo.class, internetStar.getVid()));
        }
        return internetStarCutPage;
    }

    @Override
    @Transactional
    public void createAuctionStar(String userId, AuctionInternetStarType type) {
        if (type == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"申请类型不存在");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (user.getHasInternetStarReview() || user.getHasCelebrityPass() || user.getHasInternetStarPass()) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "你已提交申请");
        }
        //没有通过视频聊天且没有上传视频
        if (!user.getHasVideoChatPass() && !user.getHasVideoUpload()) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "个人视频还没提交！");
        }
        //查询视频文件
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("userId", userId);
        paramMap.put("status", AuditStatus.CREATE.getCode());
        UserVideo userVideo = baseRepository.executeHql("from UserVideo where applierId = :userId and status = :status", paramMap);
        if (userVideo == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "个人视频还没提交！");
        }
        //将视频设置为等候审核
        userVideo.setStatus(AuditStatus.AWAIT.getCode());
        baseRepository.updateObj(userVideo);
        //保存审核信息
        InternetStar star = new InternetStar();
        star.setVid(userVideo.getId());
        star.setType(type.getCode());
        star.setApplierId(userId);
        star.setStatus(AuditStatus.CREATE.getCode());
        baseRepository.saveObj(star);
        user.addState(BitStateUtil.OP_HAS_STAR_REVIEW);
        baseRepository.updateObj(user);
    }

    @Override
    @Transactional
    public void addUserAuthentication(@RequestParam("frontPhoto") MultipartFile[] frontPhoto, @RequestParam("backPhoto") MultipartFile[] backPhoto,
                                      @RequestParam("halfPhoto") MultipartFile[] halfPhoto,
                                      String userId, String name, String cardNum) {

        String hql = "from UserAuthentication where applierId = :applierId ";
        Map<String, Object> map = new HashMap<>();
        map.put("applierId", userId);
        UserAuthentication userAuthentication = baseRepository.executeHql(hql, map);

        List<String> frontList;
        List<String> backList;
        List<String> halfList;
        try {
            frontList = fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.USER_ID_CARD_PHOTO, userId, frontPhoto);
            backList = fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.USER_ID_CARD_PHOTO, userId, backPhoto);
            halfList = fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.USER_ID_CARD_PHOTO, userId, halfPhoto);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "图片上传异常，请联系客服！");
        }

        if (userAuthentication == null) {
            UserAuthentication u = new UserAuthentication();
            u.setApplierId(userId);
            u.setName(name);
            u.setCardNum(cardNum);
            u.setStatus(AuditStatus.CREATE.getCode());

            u.setFrontPhoto(frontList.get(0));
            u.setBackPhoto(backList.get(0));
            u.setHalfPhoto(halfList.get(0));
            baseRepository.saveObj(u);

            UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, userId);
            userInfoBase.addState(BitStateUtil.OP_HAS_AUTHENTICATION_SUBMIT);
            baseRepository.updateObj(userInfoBase);
        } else {
            userAuthentication.setName(name);
            userAuthentication.setCardNum(cardNum);
            userAuthentication.setStatus(AuditStatus.CREATE.getCode());
            userAuthentication.setAuditorId(null);
            userAuthentication.setAuditTime(null);
            userAuthentication.setRemark(null);
            userAuthentication.setFrontPhoto(frontList.get(0));
            userAuthentication.setBackPhoto(backList.get(0));
            userAuthentication.setHalfPhoto(halfList.get(0));
            baseRepository.updateObj(userAuthentication);

            UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, userId);
            userInfoBase.addState(BitStateUtil.OP_HAS_AUTHENTICATION_SUBMIT);
            baseRepository.updateObj(userInfoBase);
        }
    }

    public UserAuthentication getUserAuthentication(String userId) {
        String hql = "from UserAuthentication where applierId = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return baseRepository.executeHql(hql, map);
    }

    @Override
    public List<UserSimpleInfoVO> queryUserSimpleInfo() {
        String hql = "select new com.war4.vo.UserSimpleInfoVO(id,nickName,token,userPhotoHead) from UserInfoBase where internalFlag = 1 ";
        return baseRepository.queryHqlResult(hql, null, 0, CutPage.MAX_COUNT);
    }

    @Override
    public List<UserInfoBase> queryUserByBitState(Long... bitState) {
        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT * FROM user_info_base WHERE ");
        Map<String, Object> paramMap = baseRepository.paramMap();
        for (int i = 0; i < bitState.length; i++) {
            if (i > 0) {
                sql.append(" OR ");
            }
            sql.append(" bit_state & :bitState").append(i).append(" != 0 ");
            paramMap.put("bitState" + i, bitState[i]);
        }
        return baseRepository.querySqlResult(sql, paramMap, UserInfoBase.class, 0, CutPage.MAX_COUNT);
    }

    private String jsonArrayToString(JSONArray jsonArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jsonArray.size(); i++) {

            if (i == 0) {
                String str = jsonArray.getObject(i, String.class);
                stringBuilder.append(str);
            } else {
                String str = jsonArray.getObject(i, String.class);
                stringBuilder.append(",").append(str);
            }
        }
        return stringBuilder.toString();
    }

}
