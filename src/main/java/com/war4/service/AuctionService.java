package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.*;
import com.war4.vo.AuditParamVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 竞拍活动服务
 * Created by hh on 2017.10.30 0030.
 */
public interface AuctionService {
    /**
     * 申请竞拍
     * @param files 图片
     * @param auction
     */
    void applyAuction(MultipartFile[] files,Auction auction);

    /**
     * 编辑竞拍
     */
    void editAuction(MultipartFile[] files,Auction auction);

    //获取用户进行中的竞拍
    Auction getProceedAuctionByUser(String userId);

    CutPage<Auction> queryAllAuction(Integer status,Integer type,Integer page,Integer limit);

    //修改竞拍内容
    void updateAuction(Auction auction);

    //竞拍通过
    void updateAuctionState(AuditParamVO vo);

    Auction getAuction(Long id,boolean relation);
    Auction getAuction(Long id);

    //查询用户参与过的竞拍
    CutPage<Auction> queryUserJoinAuction(String userId,Integer page,Integer limit);

    //查询用户发起的竞拍
    CutPage<Auction> queryUserAuction(String userId,Integer page,Integer limit);

    //设置竞拍为评价
    void setAuctionOrderEvaluate(String orderId);

    //更新竞拍围观人数
    void addAuctionViewCount(Long auctionId);

    //处理竞拍
    void disposeFinishAuction(Long auctionId);
    void disposeAuctionFinish(Long auctionId);

    //检查用户押金
    boolean checkUserDeposit(Long auctionId, String userId);

    //获取用户押金
    AuctionDeposit getUserDeposit(Long auctionId, String userId);

    /**
     * 查询竞拍押金记录
     * @param auctionId     竞拍ID
     * @param excludeUser   排除用户
     */
    CutPage<AuctionDeposit> queryAuctionDeposit(Long auctionId, String excludeUser,Integer page,Integer limit);

    //创建押金订单
    String createAuctionDeposit(Long auctionId,String userId);

    //根据orderId获取押金订单
    AuctionDeposit getAuctionDepositByOrderId(String orderId);

    //根据orderId获取竞拍订单
    AuctionItemOrder getAuctionItemOrderByOrderId(String orderId);

    //根据用户和竞拍获取订单
    AuctionOrder getAuctionOrderByUser(Long auctionId);

    //订单超时处理
    void disposeAuctionOrderCancel(AuctionItemOrder auctionItemOrder);

    //创建竞拍明细
    void createAuctionItem(AuctionItem auctionItem);
    //创建竞拍明细订单
    AuctionItemOrder createAuctionItemOrder(AuctionItem auctionItem);

    //获取出价明细
    AuctionItem getAuctionItem(Long itemId);

    CutPage<AuctionItem> queryActionItems(Long auctionId,Integer page,Integer limit);

    //查询结束竞拍
    List<Auction> queryFinishAuction();

    //查询指定时间指定状态竞拍订单
    List<AuctionItemOrder> queryOverdueAuctionOrder(Integer status, Integer hour);

    //押金订单成功
    void setDepositOrderPaySuccess(String orderId);
    //竞拍订单设置成功
    void setAuctionItemOrderPaySuccess(String orderId);

    Map<String,Auction> queryAuctionMapByUser(Collection userIds);

    //查询进行中的auction
    List<Auction> queryProceedAuction(String keyword);

    //查询竞拍订单
    CutPage<AuctionOrder> queryAllAuctionOrder(Long auctionId, Integer state, Integer page, Integer limit);

    //查询竞拍明细订单
    CutPage<AuctionItemOrder> queryAuctionItemOrder(Long auctionId, String userId, Integer state, Long auctionItemId, Integer page, Integer limit);
}
