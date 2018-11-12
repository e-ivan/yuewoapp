package com.war4.controller;

import com.alibaba.fastjson.JSON;
import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.TaskListType;
import com.war4.pojo.Auction;
import com.war4.pojo.AuctionItem;
import com.war4.pojo.AuctionItemOrder;
import com.war4.pojo.TaskList;
import com.war4.util.UserContext;
import com.war4.vo.SuggestionVO;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 竞拍控制器
 * Created by hh on 2017.10.31 0031.
 */
@Controller
@RequestMapping(value = "auction")
public class AuctionController extends BaseController {


    //申请竞拍
    @RequestMapping(value = "applyAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response applyAuction(@RequestParam("files") MultipartFile[] files, Auction auction) throws Exception {
        if (StringUtils.isBlank(auction.getApplierId())) {
            auction.setApplierId(UserContext.getUserId());
            auction.setType(null);
        }
        auctionService.applyAuction(files, auction);
        return new Response("竞拍已提交！");
    }

    //编辑竞拍
    @RequestMapping(value = "editAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response editAuction(@RequestParam("files") MultipartFile[] files, Auction auction) throws Exception {
        auctionService.editAuction(files, auction);
        return new Response("竞拍已提交！");
    }

    //查询所有竞拍
    @RequestMapping(value = "queryAllAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllAuction(Integer status, Integer type, String lng, String lat, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(auctionService.queryAllAuction(status, type, page, limit));
    }

    //查询进行中的竞拍
    @RequestMapping(value = "queryProceedAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryProceedAuction(String keyword) throws Exception {
        System.out.println(keyword);
        List<Auction> auctions = auctionService.queryProceedAuction(keyword);
        List<SuggestionVO> collect = auctions.stream().map(
                a -> new SuggestionVO(a.getUser().getNickName(), formatAuctionData(a)))
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("query", keyword);
        map.put("suggestions", collect);
        return new ObjectResponse<>(map);
    }

    private static Object formatAuctionData(Auction auction) {
        //显示内容   昵称 地址 用户手机号码
        return JSON.parse("{\"auctionId\":" + auction.getId() + ",\"site\":\"" + auction.getSite()
                + "\",\"nickname\":\"" + auction.getUser().getNickName() + "\",\"phone\":\""+auction.getUser().getPhone()
                +"\",\"userId\":\""+auction.getApplierId()+"\",\"finalTime\":\""+ DateFormatUtils.format(auction.getFinalTime(),"yyyy年MM月dd日HH时")+"\"}");
    }

    //获取竞拍内容
    @RequestMapping(value = "getAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response getAuction(Long auctionId, boolean onlyAuction) throws Exception {
        return new ObjectResponse<>(auctionService.getAuction(auctionId, !onlyAuction));
    }

    //修改竞拍内容
    @RequestMapping(value = "updateAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateAuction(Auction auction) throws Exception {
        auctionService.updateAuction(auction);
        return new Response("修改成功！");
    }

    //创建竞拍押金订单
//    @RequestMapping(value = "createAuctionDeposit",method = RequestMethod.POST)
    public
    @ResponseBody
    Response createAuctionDeposit(Long auctionId, String userId) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", auctionService.createAuctionDeposit(auctionId, userId));
        return new ObjectResponse<>(map);
    }

    //TODO 将来废弃 创建竞拍出价明细
//    @RequestMapping(value = "createAuctionItem",method = RequestMethod.POST)
    public
    @ResponseBody
    Response createAuctionItem(AuctionItem item) throws Exception {
        auctionService.createAuctionItem(item);
        return new Response("参与成功");
    }

    //创建竞拍订单
    @RequestMapping(value = "createAuctionItemOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createAuctionOrder(AuctionItem item) throws Exception {
        return new ObjectResponse<>(auctionService.createAuctionItemOrder(item));
    }

    //查询竞拍出价明细
    @RequestMapping(value = "queryActionItems", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryActionItems(Long auctionId, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(auctionService.queryActionItems(auctionId, page, limit));
    }

    //获取竞拍订单
    @RequestMapping(value = "getUserAuctionOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response getUserActionOrder(Long auctionId) throws Exception {
        return new ObjectResponse<>(auctionService.getAuctionOrderByUser(auctionId));
    }

    //查询用户参与的竞拍
    @RequestMapping(value = "queryUserJoinAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryUserJoinAuction(Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(auctionService.queryUserJoinAuction(UserContext.getUserId(), page, limit));
    }

    //查询用户发起的竞拍
    @RequestMapping(value = "queryUserAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryUserAuction(Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(auctionService.queryUserAuction(UserContext.getUserId(), page, limit));
    }

    //查询所有竞拍成功订单
    @RequestMapping(value = "queryAllAuctionOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllAuctionOrder(Long auctionId,Integer state,Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(auctionService.queryAllAuctionOrder(auctionId,state,page, limit));
    }

    //查询退款订单
    @RequestMapping(value = "queryAuctionRefundOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAuctionRefundOrder(Integer page, Integer limit) throws Exception {
        CutPage<TaskList> cutPage = taskListService.queryTaskList(TaskList.CREATE, TaskListType.AUCTION_ORDER_REFUND, page, limit);
        List<TaskList> taskLists = cutPage.getiData();
        List<String> orderIds = taskLists.stream().map(TaskList::getObjectId).collect(Collectors.toList());
        Map<String, AuctionItemOrder> orderMap = baseRepository.queryObjMap(AuctionItemOrder.class, orderIds, "orderId");
        taskLists.forEach(t -> t.setObj(orderMap.get(t.getObjectId())));
        return new ObjectResponse<>(cutPage);
    }

    //获取竞拍订单明细
    @RequestMapping(value = "queryAuctionItemOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAuctionItemOrder(Long auctionId,String userId,Integer state,Long auctionItemId,Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(auctionService.queryAuctionItemOrder(auctionId, userId,state,auctionItemId, page, limit));
    }

    //提前结束
    @RequestMapping(value = "finishAuction", method = RequestMethod.POST)
    public
    @ResponseBody
    Response finishAuction(Long auctionId, String password) throws Exception {
        super.checkPassword(password);
        auctionService.disposeAuctionFinish(auctionId);
        return new Response("已结束竞拍！");
    }


}
