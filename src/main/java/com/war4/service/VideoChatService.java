package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.VideoChat;
import com.war4.pojo.VideoChatItem;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/12/7.
 */
public interface VideoChatService {
    void createVideoChat(String userId);
    void updateVideoChat(VideoChat videoChat);
    CutPage<VideoChat>queryVideoChatList(Integer page, Integer limit);
    CutPage<VideoChat>queryVideoChatListForServer(String keyword,Integer page, Integer limit);
    CutPage<VideoChatItem>queryMyCreatedVideoChat(String userId,Integer page,Integer limit);
    CutPage<VideoChatItem>queryMyAcceptVideoChat(String userId,Integer page,Integer limit);
    CutPage<VideoChatItem>queryVideoChatItemList(Long chatId, Integer payType, Integer state, Integer page, Integer limit);

    VideoChat getUserVideoChat(String userId);
    void updateVideoChatSetting(String userId,Boolean onOff);
    void addVideoChatReport(Long cid,String createUserId,String reportUserId,Integer type);
    Map<String,Object> createMyVideoChatOrder(Long cid, String createUserId, String acceptUserId, String vid, Date serverTime);
    Map<String,Object> updateMyVideoChatOrder(Long chatId, Date now,String userId);

    /**
     * 设置房间结束
     * @param vid  通话视频唯一标识
     * @param hangUpUserId  挂断方
     */
    void updateVideoChatOver(String vid,String hangUpUserId);

    /**
     * 设置订单为支付成功
     * @param
     */
    void setOrderPaySuccess(String orderId);

    //设置视频聊天订单为评价
    void setVideoChatOrderEvaluate(String orderId);

    CutPage<VideoChat> queryVideoChatForChatState();
    //查询过期未评价订单
    List<VideoChatItem> queryOverdueVideoOrder(Integer status,Integer hour);
}
