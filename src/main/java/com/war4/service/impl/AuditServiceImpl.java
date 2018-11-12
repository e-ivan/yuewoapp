package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.PropertiesConfig;
import com.war4.base.SystemParameters;
import com.war4.enums.AuditStatus;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.AuctionInternetStarType;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.*;
import com.war4.service.AuditService;
import com.war4.service.UserPersonalDetailService;
import com.war4.service.UserRatioService;
import com.war4.service.VideoChatService;
import com.war4.util.BitStateUtil;
import com.war4.vo.AuditParamVO;
import com.war4.vo.PushMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hh on 2017.10.30 0030.
 */
@Service
public class AuditServiceImpl extends BaseService implements AuditService {
    @Autowired
    private UserPersonalDetailService personalDetailService;
    @Autowired
    private UserRatioService userRatioService;
    @Autowired
    private VideoChatService videoChatService;

    @Override
    @Transactional
    public void updateAuthentication(AuditParamVO vo) {
        UserAuthentication ua = baseRepository.getObjById(UserAuthentication.class, vo.getId());
        if (ua == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "此记录不存在！");
        }
        if (!ua.getStatus().equals(AuditStatus.CREATE.getCode())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "已审核！");
        }
        ua.setAuditorId(vo.getAuditorId());
        ua.setAuditTime(new Date());
        ua.setRemark(vo.getRemark());
        ua.setStatus(vo.isPass() ? AuditStatus.PASS.getCode() : AuditStatus.REFUSE.getCode());
        baseRepository.updateObj(ua);

        UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, ua.getApplierId());
        String msg ;

        if (vo.isPass()) {
            userInfoBase.addState(BitStateUtil.OP_HAS_AUTHENTICATION_PASS);
            msg = "你的身份认证已通过审核";
        } else {
            userInfoBase.removeState(BitStateUtil.OP_HAS_AUTHENTICATION_PASS);
            userInfoBase.removeState(BitStateUtil.OP_HAS_AUTHENTICATION_SUBMIT);
            msg = "你的身份认证未能通过审核，身份认证信息需清晰可见，建议你重新申请";
        }
        baseRepository.updateObj(userInfoBase);
        PushMsgVO pmv = new PushMsgVO(ua.getApplierId(), ua.getAuditorId(), SystemParameters.MESSAGE_AUTHENTICATION_AUDIT, msg, ua.getId(), MessageLogType.AUTHENTICATION, true);
        ac.publishEvent(new PushEvent(pmv));

    }

    @Override
    @Transactional
    public void updateUserVideo(AuditParamVO vo) {
        UserVideo v = baseRepository.getObjById(UserVideo.class, vo.getId());
        if (v == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "此记录不存在！");
        }
        if (!AuditStatus.CREATE.getCode().equals(v.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "已审核！");
        }

        v.setAuditorId(vo.getAuditorId());
        v.setAuditTime(new Date());
        v.setRemark(vo.getRemark());
        v.setStatus(vo.isPass() ? AuditStatus.PASS.getCode() : AuditStatus.REFUSE.getCode());
        baseRepository.updateObj(v);
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, v.getApplierId());
        this.createVideoChat(v,user,vo.isPass());
        //审核状态归0
        baseRepository.updateObj(user);
    }

    private void createVideoChat(UserVideo userVideo,UserInfoBase user, boolean pass){
        //查询视频聊天室
        VideoChat videoChat = videoChatService.getUserVideoChat(userVideo.getApplierId());
        //移除状态
        user.removeState(BitStateUtil.OP_HAS_VIDEO_UPLOAD);
        user.removeState(BitStateUtil.OP_HAS_VIDEO_CHAT_REVIEW);
        String msg;
        if (pass) {
            user.addState(BitStateUtil.OP_HAS_OPEN_VIDEO_CHAT);
            //处理用户分成比例
            UserRatio userRatio = userRatioService.getLatestByUser(user.getId());
            if (userRatio == null) {
                userRatio = new UserRatio(user.getId(), PropertiesConfig.getVideoChatCostProportion()[0], BigDecimal.ZERO);
            } else {
                userRatio.setVideoChatRatio(PropertiesConfig.getVideoChatCostProportion()[0]);
            }
            userRatioService.save(userRatio);
            if (videoChat == null) {
                videoChat = new VideoChat();
                videoChat.setUserId(userVideo.getApplierId());
                videoChat.setVideoId(userVideo.getId());
                videoChat.setStatus(AuditStatus.PASS.getCode());
                videoChat.setOnOff(!(user.getHasCelebrityPass() || user.getHasInternetStarPass()));
                baseRepository.saveObj(videoChat);
            } else {
                videoChat.setOnOff(!(user.getHasCelebrityPass() || user.getHasInternetStarPass()));
                videoChat.setStatus(AuditStatus.PASS.getCode());
                baseRepository.updateObj(videoChat);
            }
            user.addState(BitStateUtil.OP_HAS_VIDEO_CHAT_PASS);
            user.addState(BitStateUtil.OP_HAS_OPEN_VIDEO_CHAT);
            msg = "恭喜您的个人视频已通过审核！";
        } else {
            user.removeState(BitStateUtil.OP_HAS_VIDEO_CHAT_PASS);
            user.removeState(BitStateUtil.OP_HAS_OPEN_VIDEO_CHAT);
            if (videoChat != null) {
                videoChat.setStatus(AuditStatus.REFUSE.getCode());
                baseRepository.updateObj(videoChat);
            }
            msg = "很遗憾您的个人视频没有通过审核！";
        }
        PushMsgVO pmv = new PushMsgVO(userVideo.getApplierId(), userVideo.getAuditorId(), SystemParameters.MESSAGE_VIDEO_CHAT_AUDIT, msg, MessageLogType.VIDEO_CHAT, true);
        ac.publishEvent(new PushEvent(pmv));
    }

    @Override
    @Transactional
    public void updateInternetStar(AuditParamVO vo) {
        InternetStar s = baseRepository.getObjById(InternetStar.class, vo.getId());
        if (s == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "此记录不存在！");
        }
        if (!AuditStatus.CREATE.getCode().equals(s.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "已审核！");
        }
        s.setAuditorId(vo.getAuditorId());
        s.setAuditTime(new Date());
        s.setRemark(vo.getRemark());
        s.setStatus(vo.isPass() ? AuditStatus.PASS.getCode() : AuditStatus.REFUSE.getCode());
        baseRepository.updateObj(s);

        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, s.getApplierId());
        String msg ;

        UserVideo userVideo = baseRepository.getObjById(UserVideo.class, s.getVid());
        if (vo.isPass()) {
            user.addState(AuctionInternetStarType.CELEBRITY.getCode().equals(s.getType()) ?
                    BitStateUtil.OP_HAS_CELEBRITY_PASS : BitStateUtil.OP_HAS_INTERNETSTAR_PASS);
            //设置红人印象
            personalDetailService.setUserDateImpression(user.getId());
            //设置分成比例
            UserRatio userRatio = userRatioService.getLatestByUser(user.getId());
            if (userRatio == null) {
                userRatio = new UserRatio(user.getId(), BigDecimal.ZERO, PropertiesConfig.getAuctionRewardProportion()[0]);
            } else {
                userRatio.setAuctionRatio(PropertiesConfig.getAuctionRewardProportion()[0]);
            }
            userRatioService.save(userRatio);
            //创建视频聊天室
            if (userVideo == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "此记录不存在！");
            }
            if (!AuditStatus.AWAIT.getCode().equals(userVideo.getStatus())) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "审核视频失败！");
            }

            userVideo.setAuditorId(vo.getAuditorId());
            userVideo.setAuditTime(new Date());
            userVideo.setRemark("网红名人通过默认开通");
            userVideo.setStatus(AuditStatus.PASS.getCode());
            baseRepository.updateObj(userVideo);
            msg = "恭喜您的红人已通过审核！";
        } else {
            msg = "很遗憾您的红人没有通过审核！";
        }

        this.createVideoChat(userVideo,user,vo.isPass());
        PushMsgVO pmv = new PushMsgVO(s.getApplierId(), s.getAuditorId(), SystemParameters.MESSAGE_INTERNET_STAR_AUDIT, msg, s.getId(), MessageLogType.AUCTION, true);
        ac.publishEvent(new PushEvent(pmv));

        //审核状态归0
        user.removeState(BitStateUtil.OP_HAS_STAR_REVIEW);
        baseRepository.updateObj(user);
    }

}
