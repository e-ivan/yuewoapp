package com.war4.controller;

import com.war4.base.*;
import com.war4.enums.VideoChatStatus;
import com.war4.pojo.VideoChat;
import com.war4.pojo.VideoChatItem;
import com.war4.util.UserContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping(value = "videoChat")
public class VideoChatController extends BaseController {


    /**
     * 申请开通视频聊天
     */
    @RequestMapping(value = "addVideoChat", method = RequestMethod.POST)
    public
    @ResponseBody
    Response addVideoChat(String userId) {
//        videoChatService.createVideoChat(userId);
        return new ObjectResponse<>("已申请!");
    }

    /**
     * 修改聊天室参数
     */
    @RequestMapping(value = "updateVideoChat", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateVideoChat(VideoChat videoChat) {
        videoChatService.updateVideoChat(videoChat);
        return new ObjectResponse<>("修改成功!");
    }

    /**
     * 视频聊天列表
     */
    @RequestMapping(value = "queryVideoChatList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryVideoChatList(Integer page, Integer limit) {
        CutPage<VideoChat> videoChatCutPage = videoChatService.queryVideoChatList(page, limit);
        return new ObjectResponse<>(videoChatCutPage);
    }

    /**
     * 视频聊天列表(后台) 数据分析用
     */
    @RequestMapping(value = "queryVideoChatListForServer", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryVideoChatListForServer(String keyword, Integer page, Integer limit) {
        CutPage<VideoChat> videoChatCutPage = videoChatService.queryVideoChatListForServer(keyword, page, limit);
        return new ObjectResponse<>(videoChatCutPage);
    }

    /**
     * 创建视频聊天
     */
    @RequestMapping(value = "createMyVideoChatOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createMyVideoChat(Long cid, String createUserId, String acceptUserId, String vid, Long serverTime) {
        Map<String, Object> map = videoChatService.createMyVideoChatOrder(cid, createUserId, acceptUserId, vid, new Date(serverTime));
        return new ObjectResponse<>(map);
    }

    /**
     * 更新视频聊天
     */
    @RequestMapping(value = "updateMyVideoChatOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateMyVideoChat(Long chatId, Long serverTime, String userId) {
        try {
            Map<String, Object> map = videoChatService.updateMyVideoChatOrder(chatId, new Date(serverTime), userId);
            return new ObjectResponse<>(map);
        } catch (BusinessException e) {
            VideoChatItem videoChatItem = baseRepository.getObjById(VideoChatItem.class, chatId);
            VideoChat videoChat = baseRepository.getObjById(VideoChat.class, videoChatItem.getCid());
            videoChat.setChatState(VideoChatStatus.OFFLINE.getCode());
            baseRepository.updateObj(videoChat);
            throw e;
        }
    }

    /**
     * 设置视频聊天为结束
     */
    @RequestMapping(value = "updateVideoChatOver", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateVideoChatOver(String vid) {
        try {
            videoChatService.updateVideoChatOver(vid, UserContext.getUserId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ObjectResponse<>("视频聊天已结束");
    }


    /**
     * 查所有视频聊天的列表
     */
    @RequestMapping(value = "queryVideoChatItemList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryVideoChatItemList(Long chatId,Integer payType,Integer state,Integer page, Integer limit) {
        CutPage<VideoChatItem> videoChatItemCutPage = videoChatService.queryVideoChatItemList(chatId,payType,state,page, limit);
        return new ObjectResponse<>(videoChatItemCutPage);
    }

    /**
     * 查我邀约视频的列表
     */
    @RequestMapping(value = "queryMyCreatedVideoChat", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyCreatedVideoChat(String userId, Integer page, Integer limit) {
        CutPage<VideoChatItem> videoChatItemCutPage = videoChatService.queryMyCreatedVideoChat(userId, page, limit);
        return new ObjectResponse<>(videoChatItemCutPage);
    }

    /**
     * 查我收到视频的列表
     */
    @RequestMapping(value = "queryMyAcceptVideoChat", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyAcceptVideoChat(String userId, Integer page, Integer limit) {
        CutPage<VideoChatItem> videoChatItemCutPage = videoChatService.queryMyAcceptVideoChat(userId, page, limit);
        return new ObjectResponse<>(videoChatItemCutPage);
    }

    /**
     * 查询个人视频聊天
     */
    @RequestMapping(value = "queryMyVideoChat", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyVideoChat(String userId) {
        return new ObjectResponse<>(videoChatService.getUserVideoChat(userId));
    }

    /**
     * 设置视频聊天开关
     */
    @RequestMapping(value = "updateVideoChatSetting", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateVideoChatSetting(String userId, boolean onOff) {
        videoChatService.updateVideoChatSetting(userId, onOff);
        return new ObjectResponse<>("操作成功!");
    }

    /**
     * 视频聊天举报
     */
    @RequestMapping(value = "addVideoChatReport", method = RequestMethod.POST)
    public
    @ResponseBody
    Response addVideoChatReport(Long cid, String createUserId, String reportUserId, Integer type) {
        videoChatService.addVideoChatReport(cid, createUserId, reportUserId, type);
        return new ObjectResponse<>("举报成功!");
    }


}
