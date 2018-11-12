package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.*;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.ActivityBid;
import com.war4.pojo.ActivityBidItem;
import com.war4.pojo.TaskList;
import com.war4.pojo.UserInfoBase;
import com.war4.service.*;
import com.war4.util.NumberUtil;
import com.war4.util.OrderUtil;
import com.war4.vo.PushMsgVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/7.
 */
@Service
public class ActivityBidServiceImpl extends BaseService implements ActivityBidService {

    @Autowired
    private BaseOrderService baseOrderService;
    @Autowired
    private AssembleService assembleService;
    @Autowired
    private TaskListService taskListService;
    @Autowired
    private UserCouponService userCouponService;

    @Override
    @Transactional
    public ActivityBid addActivityBid(ActivityBid activityBid) {
        ActivityBid actBid = new ActivityBid();
        actBid.setUserId(activityBid.getUserId());
        actBid.setTitle(activityBid.getTitle());
        actBid.setContent(activityBid.getContent());
        actBid.setType(ActivityBid.TYPE_ONEYUAN);
        actBid.setStartTime(activityBid.getStartTime());
        actBid.setEndTime(activityBid.getEndTime());
        actBid.setRatedBid(activityBid.getRatedBid());
        actBid.setBidPrice(activityBid.getBidPrice());
        actBid.setFullProceed(ActivityBid.FLAG_NOTENOUGH);
        actBid.setOdds(activityBid.getOdds());
        actBid.setState(ActivityBid.STATE_CREATE);
        actBid.setPic(activityBid.getPic());
        actBid.setObjectId(activityBid.getObjectId());
        baseRepository.saveObj(actBid);
        return actBid;
    }

    @Override
    @Transactional
    public void deleteActivityBid(Long bId) {
        ActivityBid activityBid = baseRepository.getObjById(ActivityBid.class, bId);
        CutPage<ActivityBidItem>activityBidItemCutPage = queryActivityBidItemList(bId,null,0,Integer.MAX_VALUE);
        if(activityBidItemCutPage.getiData().size() != 0){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "已产生订单记录，无法删除");
        }
        //在创建状态时
        baseRepository.physicsDelete(activityBid);
    }

    @Override
    public void updateActivityBidState(Long id, Byte state) {
        ActivityBid actBid = baseRepository.getObjById(ActivityBid.class, id);
        if (actBid == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "不存在的投标活动！");
        }
        actBid.setState(state);
        baseRepository.updateObj(actBid);
    }


    @Override
    public CutPage<ActivityBid> queryActivityBidList(Byte type, Integer state, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();

        hql.append("from ActivityBid where 1 = 1 ");
        if(type != null){
            hql.append("and type = :type ");
            map.put("type", type);
        }

        if(state != null){
            if (ActivityBidStatus.CREATE.getCode().equals(state)) {
                hql.append("and state = :state ");
                map.put("state", ActivityBid.STATE_CREATE);
            } else if (ActivityBidStatus.BEGIN.getCode().equals(state)) {
                hql.append("and ( state = :state1 or state = :state2 ) ");
                map.put("state1", ActivityBid.STATE_GOING);
                map.put("state2", ActivityBid.STATE_FULLPEOPLE);
            } else if (ActivityBidStatus.FINISH.getCode().equals(state)) {
                hql.append("and state = :state ");
                map.put("state", ActivityBid.STATE_OVER);
            } else if (ActivityBidStatus.GOING.getCode().equals(state)) {
                hql.append("and state = :state ");
                map.put("state", ActivityBid.STATE_GOING);
            } else if (ActivityBidStatus.FULL.getCode().equals(state)) {
                hql.append("and state = :state ");
                map.put("state", ActivityBid.STATE_FULLPEOPLE);
            } else if (ActivityBidStatus.ALL.getCode().equals(state)) {
                hql.append("and ( state = :state1 or state = :state2 or state = :state3 ) ");
                map.put("state1", ActivityBid.STATE_GOING);
                map.put("state2", ActivityBid.STATE_FULLPEOPLE);
                map.put("state3", ActivityBid.STATE_OVER);
            }

        }
        hql.append("order by created desc ");
        return baseRepository.executeHql(hql, map, page, limit);
    }

    //查询进行的活动
    @Override
    public CutPage<ActivityBid> queryActivityBidListByDate(Byte type, Byte state,Date date, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();

        hql.append("from ActivityBid where 1 = 1 ");
        if(type != null){
            hql.append("and type = :type ");
            map.put("type", type);
        }
        if(state != null){
            hql.append("and state = :state ");
            map.put("state", state);
        }

        if(date != null){
            hql.append("and startTime <= :date and endTime > :date ");
            map.put("date", date);
        }

        hql.append("order by created desc ");
        return baseRepository.executeHql(hql, map, page, limit);
    }


    @Override
    public CutPage<ActivityBid> queryActivityBidListByBeginToLottery(Byte type, Integer state,Date date, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();

        hql.append("from ActivityBid where 1 = 1 ");
        if(type != null){
            hql.append("and type = :type ");
            map.put("type", type);
        }
        if(state != null) {
            if (ActivityBidStatus.BEGIN.getCode().equals(state)) {
                hql.append("and ( state = :state1 or state = :state2 ) ");
                map.put("state1", ActivityBid.STATE_GOING);
                map.put("state2", ActivityBid.STATE_FULLPEOPLE);
            }
        }

        if(date != null){
            hql.append("and endTime <= :date ");
            map.put("date", date);
        }

        hql.append("order by created desc ");
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public ActivityBid getActivityBid(Long bId, boolean correlation, String userId) {
        ActivityBid bid = baseRepository.getObjById(ActivityBid.class, bId);
        if (bid == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "活动不存在ID:" + bId);
        }
        if (correlation) {
            bid.setCurrentUser(userId);
            assembleService.assembleObject(bid);
        }
        return bid;
    }

    @Override
    public ActivityBid getActivityBid(Long bId) {
        return this.getActivityBid(bId, false, null);
    }


    @Override
    @Transactional
    public String createActivityBidItem(Long bId, String userId, Integer count) {
        //查询投标活动
        ActivityBid activityBid = this.getActivityBid(bId);
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "用户不存在！");
        }
        //当活动为已开始时
        if (activityBid.getState().equals(ActivityBid.STATE_GOING)) {
            //获取活动中的除取消状态的明细，判断是否有本次参加用户
            CutPage<ActivityBidItem> cutPage = this.queryActivityBidItemList(bId, userId, true, 0, CutPage.MAX_COUNT);
            //没有，创建活动订单明细
            if (cutPage.getTotalCount() <= 0) {
                ActivityBidItem item = new ActivityBidItem();
                item.setHeadImg(user.getUserPhotoHead());
                item.setUserId(userId);
                item.setNickname(user.getNickName());
                item.setBidId(bId);
                item.setStartTime(activityBid.getStartTime());
                item.setJoinCount(count <= 0 ? 1 : count);
                item.setCurrentCount(1);
                item.setState(ActivityBidItem.CREATE);
                item.setPrice(activityBid.getBidPrice());
                item.setPayMoney(item.getPrice());
                String orderId = OrderUtil.ACTIVITY_BID + OrderUtil.getUUID();
                item.setOrderId(orderId);
                baseRepository.saveObj(item);
                //创建通用订单
                baseOrderService.addBaseOrder(orderId, OrderType.BID_ORDER,userId);
                this.disposeBidJoinCount(activityBid, true);
                baseRepository.updateObj(activityBid);
                //返回订单id
                return orderId;
            }
            //有记录且为创建状态，返回订单id
            ActivityBidItem item = cutPage.getiData().get(0);
            if (item.getState().equals(ActivityBidItem.CREATE)) {
                //如果次数参次数改变
                if (!item.getJoinCount().equals(count)) {
                    item.setJoinCount(count);
                    baseRepository.updateObj(item);
                }
                return item.getOrderId();
            }
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "你已参加该活动");
        }
        throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "活动已结束！");
    }

    @Override
    @Transactional
    public void continuousJoinActivityBid(Long bId) {
        ActivityBid activityBid = this.getActivityBid(bId);
        if (activityBid == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"活动不存在");
        }
        CutPage<ActivityBidItem> cutPage = this.queryNextActivityBidItem(0, CutPage.MAX_COUNT);
        for (ActivityBidItem item : cutPage.getiData()) {
            ActivityBidItem currentItem = this.queryBidItemByUser(bId, item.getUserId());
            //如果可以参加
            if (currentItem == null && ActivityBid.STATE_GOING.equals(activityBid.getState())) {
                ActivityBidItem clone = item.clone();//克隆对象
                //将克隆对象中的当前次数+1，状态设置为进行中,关联活动对象
                clone.setCurrentCount(clone.getCurrentCount() + 1);
                clone.setBidId(bId);
                clone.setState(ActivityBidItem.WAIT);
                baseRepository.saveObj(clone);
                //将活动对象人数+1，且判断人数状态
                this.disposeBidJoinCount(activityBid, true);
                //将原对象设置为已结束状态
                item.setState(ActivityBidItem.FINISH);
                baseRepository.updateObj(item);
            }
        }
        baseRepository.updateObj(activityBid);
    }

    /**
     * 查询活动中用户参与的明细
     * @param bId
     * @param userId
     */
    private ActivityBidItem queryBidItemByUser(Long bId,String userId){
        String hql = "from ActivityBidItem where bidId = :bId and userId = :userId and state != 5";
        Map<String,Object> map = new HashMap<>();
        map.put("bId",bId);
        map.put("userId",userId);
        return baseRepository.executeHql(hql,map);
    }


    @Override
    public CutPage<ActivityBidItem> queryActivityBidItemList(Long bId, String userId, Boolean action, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(150);
        Map<String, Object> map = new HashMap<>();
        hql.append("from ActivityBidItem where bidId = :bidId ");
        map.put("bidId", bId);
        if (StringUtils.isNotBlank(userId)) {
            hql.append(" and userId = :userId ");
            map.put("userId", userId);
        }
        if (action != null) {
            if (action) {
                hql.append(" and state != 5 ");
            } else {
                hql.append(" and state = 5 ");
            }
        }
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public CutPage<ActivityBidItem> queryActivityBidItemList(Long bId, Byte state, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(150);
        Map<String, Object> map = new HashMap<>();
        hql.append("from ActivityBidItem where bidId = :bidId ");
        map.put("bidId", bId);
        if(state != null){
            hql.append("and state = :state");
            map.put("state", state);
        }
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public ActivityBidItem getActivityBidItemByOrderId(String orderId) {
        String hql = "from ActivityBidItem where orderId = :orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        return baseRepository.executeHql(hql, map);
    }

    @Override
    public CutPage<ActivityBidItem> queryNextActivityBidItem(Integer page, Integer limit) {
        String hql = "from ActivityBidItem where state = 4";
        return baseRepository.executeHql(hql, null, page, limit);
    }

    @Override
    @Transactional
    public void handleItemTimeout(Integer minute) {
        String hql = "from ActivityBidItem where state = 0 and timesTampDiff(MINUTE,created,NOW()) >= :minute";
        Map<String, Object> map = new HashMap<>();
        map.put("minute", minute);
        CutPage<ActivityBidItem> cutPage = baseRepository.executeHql(hql, map, 0, CutPage.MAX_COUNT);
        for (ActivityBidItem item : cutPage.getiData()) {
            //查询明细对应的活动
            ActivityBid bid = baseRepository.getObjById(ActivityBid.class, item.getBidId());
            if (bid != null) {
                this.disposeBidJoinCount(bid, false);
                baseRepository.updateObj(bid);
            }
            //将订单明细改为已取消状态
            item.setState(ActivityBidItem.CANCEL);
            baseRepository.updateObj(item);
        }
    }

    /**
     * 处理活动人数
     *
     * @param bid
     * @param add true 为+1  false 为-1
     */
    private ActivityBid disposeBidJoinCount(ActivityBid bid, boolean add) {
        if (add) {
            bid.setJoinBid(bid.getJoinBid() + 1);
            //判断活动是否满人，满人设置为满人状态
            if (bid.getRatedBid().compareTo(bid.getJoinBid()) <= 0) {
                bid.setState(ActivityBid.STATE_FULLPEOPLE);
            }
        } else {
            bid.setJoinBid(bid.getJoinBid() - 1);
            //将活动参与人数-1，如果状态为已满，则改为已开始状态
            if (bid.getState().equals(ActivityBid.STATE_FULLPEOPLE)) {
                bid.setState(ActivityBid.STATE_GOING);
            }
        }
        return bid;
    }

    @Override
    @Transactional
    public void handleLotteryDrawing(Long bId) {
        ActivityBid activityBid = this.getActivityBid(bId);
        CutPage<ActivityBidItem> activityBidItemCutPage = queryActivityBidItemList(activityBid.getId(), ActivityBidItem.WAIT, 0, Integer.MAX_VALUE);
        Integer joinBid = activityBidItemCutPage.getiData().size(); //有资格参加此活动的人数
        int victorCount = 0;//中奖人数
        if(joinBid >= 1) { //人数大于等于1才能开始处理
            BigDecimal joinBidBig = new BigDecimal(joinBid);
            BigDecimal odds = activityBid.getOdds();
            BigDecimal temp = joinBidBig.multiply(odds);
            Integer peopleLotteryNum;
            //中奖人数不能少于1
            if (temp.intValue() < 1) {
                peopleLotteryNum = 1;
            } else {
                peopleLotteryNum = temp.intValue();
            }
            //得出随机数
            List<Integer> random = NumberUtil.randomArray(0, joinBid - 1, peopleLotteryNum);
            for (int i = 0; i < activityBidItemCutPage.getiData().size(); i++) {
                ActivityBidItem activityBidItem = activityBidItemCutPage.getiData().get(i);
                if (random.contains(i)) {  //中奖名单处理
                    activityBidItem.setState(ActivityBidItem.VICTOR);
                    baseRepository.updateObj(activityBidItem);
                    //发优惠券
                    userCouponService.addUserCoupon(activityBidItem.getUserId(),activityBid.getObjectId(), 1);
                    victorCount ++;
                } else {  //未中奖名单处理
                    handNoLottery(activityBidItem);
                }
            }
        }
        activityBid.setVictorCount(victorCount);
        baseRepository.updateObj(activityBid);
    }

    //处理未中奖
    private void handNoLottery(ActivityBidItem activityBidItem) {

        //判断哪种方式,多场的设为继续，一场的设为结束
        Integer joinCount = activityBidItem.getJoinCount();
        Integer currentCount = activityBidItem.getCurrentCount();
        if (currentCount < joinCount) {
            activityBidItem.setState(ActivityBidItem.NEXT);
        } else {
            activityBidItem.setState(ActivityBidItem.FINISH);
            //添加到退款任务中
            TaskList taskList = new TaskList();
            taskList.setUserId(activityBidItem.getUserId());
            taskList.setObjectId(activityBidItem.getId().toString());
            taskList.setType(TaskListType.BID_REFUND.getValue());
            taskListService.createTask(taskList);
        }
        baseRepository.updateObj(activityBidItem);
    }

    @Override
    @Transactional
    public void setOrderPaySuccess(String orderId) {
        ActivityBidItem bidOrder = this.getActivityBidItemByOrderId(orderId);
        if (bidOrder != null && ActivityBidItem.CREATE.equals(bidOrder.getState())){
            bidOrder.setState(ActivityBidItem.WAIT);
            bidOrder.setPayTime(new Date());
            baseRepository.updateObj(bidOrder);
            //推送信息
            PushMsgVO pmv = new PushMsgVO(bidOrder.getUserId(),
                    null,"投标成功", "用户Id："+ bidOrder.getUserId() + "投标:" + bidOrder.getBidId() + " 成功,订单：" + bidOrder.getOrderId(),
                    bidOrder.getId().toString(),
                    MessageLogType.BID_ORDER);
            ac.publishEvent(new PushEvent(pmv));
        }
    }
}
