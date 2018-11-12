package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.BaseOrderStatus;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.OrderType;
import com.war4.pojo.*;
import com.war4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hh on 2017.11.3 0003.
 */
@Service
public class EvaluateSystemServiceImpl extends BaseService implements EvaluateSystemService {
    @Autowired
    private UserCorrelativeService correlativeService;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private VideoChatService videoChatService;
    @Autowired
    private SystemDictionaryService systemDictionaryService;

    @Override
    @Transactional
    public void createEvaluateDetailed(EvaluateDetailed detailed) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, detailed.getUtterer());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (user.getId().equals(detailed.getReceiver())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能自己评价自己");
        }

        BaseOrder order = baseRepository.getObjById(BaseOrder.class, detailed.getOrderId());
        if (order == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单不存在");
        }
        if (!BaseOrderStatus.PAY.getCode().equals(order.getStatus()) || BaseOrderStatus.EVALUATE.getCode().equals(order.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单不能评价");
        }
        SystemDictionary sd = baseRepository.getObjById(SystemDictionary.class, detailed.getEvaluateTypeId());
        if (sd == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "评价分类不存在");
        }
        EvaluateDetailed ed = new EvaluateDetailed();
        ed.setTitle(sd.getTitle());
        ed.setEvaluateTypeId(detailed.getEvaluateTypeId());
        ed.setUtterer(detailed.getUtterer());
        ed.setReceiver(detailed.getReceiver());
        ed.setOrderId(detailed.getOrderId());
        ed.setOrderType(detailed.getOrderType());
        ed.setType(sd.getValue());
        ed.setOrderType(order.getType());
        ed.setScore(detailed.getScore());
        List<Long> itemIds = detailed.getItemIds();
        ed.setItemId(JSON.toJSONString(itemIds));
        try {
            switch (sd.getValue()) {
                case 0: //系统默认
                case 1: //单选计数
                    SystemDictionaryItem i = baseRepository.getObjById(SystemDictionaryItem.class, itemIds.get(0));
                    //被评用户对应字段 + 1
                    correlativeService.addCount(i.getField(), ed.getReceiver());
                    ed.setContent(i.getTitle());
                    break;
                case 2: //多项标签选择
                    Map<String, Object> map = new HashMap<>();
                    map.put("ids", itemIds);
                    List<String> titles = baseRepository.queryHqlResult("select title from SystemDictionaryItem where id in (:ids)", map, 0, itemIds.size());
                    ed.setContent(JSON.toJSONString(titles));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"传递参数有误！");
        }
        baseRepository.saveObj(ed);
        //创建评价明细关联
        for (Long id : itemIds) {
            baseRepository.saveObj(new EvaluateDetailedItem(ed.getId(), ed.getEvaluateTypeId(), id));
        }
    }

    @Override
    @Transactional
    public void setOrderEvaluate(String orderId) {
        this.setOrderEvaluate(orderId,false);
    }

    @Override
    @Transactional
    public void setOrderEvaluate(String orderId, boolean auto) {
        BaseOrder baseOrder = baseRepository.getObjById(BaseOrder.class, orderId);
        if (baseOrder != null) {
            //如果是自动评论
            if (auto) {
                this.setDefaultEvaluate(baseOrder);
            }
            if (baseOrder.getType().equals(OrderType.AUCTION_ORDER.getCode())) {
                auctionService.setAuctionOrderEvaluate(orderId);
            }
            if (baseOrder.getType().equals(OrderType.VIDEO_CHAT_ORDER.getCode())) {
                videoChatService.setVideoChatOrderEvaluate(orderId);
            }
            baseOrder.setStatus(BaseOrderStatus.EVALUATE.getCode());
            baseRepository.updateObj(baseOrder);
        }
    }

    @Override
    public List<EvaluateDetailed> queryOrderEvaluateDetailed(String orderId) {
        String hql = "from EvaluateDetailed where orderId = :orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        CutPage<EvaluateDetailed> cutPage = baseRepository.executeHql(hql, map, 0, CutPage.MAX_COUNT);
        return cutPage.getiData();
    }

    @Transactional
    private void setDefaultEvaluate(BaseOrder order) {
        //设置默认评价
        SystemDictionary sd = systemDictionaryService.getSystemDictionaryBySN("systemDefaultEvaluate");
        EvaluateDetailed ed = new EvaluateDetailed();
        ed.setOrderType(order.getType());
        ed.setItemIds(Collections.singletonList(systemDictionaryService.querySystemDictionaryItemList(null, null, sd.getId()).getiData().get(0).getId()));
        ed.setEvaluateTypeId(sd.getId());
        ed.setUtterer(order.getCreator());
        ed.setReceiver(order.getReceiver());
        ed.setOrderId(order.getId());
        this.createEvaluateDetailed(ed);
    }
}
