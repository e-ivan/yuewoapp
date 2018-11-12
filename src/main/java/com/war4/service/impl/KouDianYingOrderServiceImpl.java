package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.war4.base.BaseService;
import com.war4.base.PropertiesConfig;
import com.war4.base.SystemParameters;
import com.war4.enums.FilmOrderStatus;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.FilmOrderEvent;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.FilmOrder;
import com.war4.service.KouDianYingOrderService;
import com.war4.service.YinghezhongService;
import com.war4.util.RequestUtil;
import com.war4.util.TimerTaskForUser;
import com.war4.vo.PushMsgVO;
import com.war4.vo.film.CinemaPlanVO;
import com.war4.vo.film.FilmOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by hh on 2017.6.14 0014.
 */
@Service
@Slf4j
public class KouDianYingOrderServiceImpl extends BaseService implements KouDianYingOrderService {

    private static final String CONN_URL = PropertiesConfig.getKouDianYingOrder();
    @Autowired
    private YinghezhongService yinghezhongService;

    @Override
    @Transactional
    public List<FilmOrderVO> queryMovieOrderById(String orderId, Timer timer) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "order_Query");
        map.put("order_id", orderId);
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        //获取订单信息
        List<FilmOrderVO> orders = JSON.parseArray(jsonObject.getString("orders"), FilmOrderVO.class);
        if (orders.size() > 0) {
            FilmOrderVO order = orders.get(0);
            Integer orderStatus = order.getOrderStatus();
            FilmOrder filmOrder = baseRepository.getObjById(FilmOrder.class, orderId);
            if (filmOrder != null) {
                //如果是第一次修改订单成功，则推送
                if (!filmOrder.getStatus().equals(FilmOrderStatus.SUCCESS.getCode()) && orderStatus.equals(FilmOrderStatus.SUCCESS.getCode())) {
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }
                        CinemaPlanVO plan = order.getPlan();
                        Integer movieLength = plan.getMovie().getMovieLength();
                        Date featureTime = plan.getFeatureTimeDate();
                        //设置订单的电影时间
                        filmOrder.setStartTime(featureTime);
                        filmOrder.setEndTime(DateUtils.addMinutes(featureTime, movieLength == 0 ? 120 : movieLength));
                        //更新订单数据到mongodb
                        yinghezhongService.updateFilmOrderVO(order.getOrderId(), order.getOrderStatus(),
                                order.getMobile(), order.getTicketNo(), order.getFinalTicketNo(), order.getFinalVerifyCode());
                    } catch (Exception e) {
                        log.warn("抠电影订单状态更新到mongodb失败 orderId {} ,原因 {}", orderId, e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                    PushMsgVO pmv = new PushMsgVO(filmOrder.getFkUserId(), null, SystemParameters.MESSAGEFILM_ORDER_PUSH_TICKET,
                            filmOrder.getFkOrderId() + "购买" + filmOrder.getCount() + "张电影票成功，价格：" + filmOrder.getPayMoney(),
                            filmOrder.getFkOrderId(), MessageLogType.FILM_ORDER, true);
                    ac.publishEvent(new PushEvent(pmv));
                    ac.publishEvent(new FilmOrderEvent(order));//电影订单事件
                    if (!Objects.equals(orderStatus, filmOrder.getStatus())) {
                        filmOrder.setStatus(orderStatus);
                        baseRepository.updateObj(filmOrder);
                    }
                }
            }
        }
        return orders;
    }

    @Override
    @Transactional
    public void confirmMovieOrder(String orderId) throws Exception {
        FilmOrder filmOrder = baseRepository.getObjById(FilmOrder.class, orderId);
        String balance = filmOrder.getMoney().toString();
        String mobile = filmOrder.getMobile();
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "order_Confirm");
        map.put("order_id", orderId);
        map.put("balance", balance);
        map.put("mobile", mobile);
        RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        filmOrderSchedule(orderId);
    }

    private void filmOrderSchedule(String orderId) {
        //开启任务，到点开始播放电影
        Timer timer = new Timer();

        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            //异步处理
            //异步处理相关文档：http://www.cnblogs.com/jevo/archive/2013/04/11/3015023.html
            FutureTask<String> future = new FutureTask<>(new Callable<String>() {
                public String call() throws Exception { //建议抛出异常
                    // 15秒执行一次
                    timer.schedule(new TimerTaskForUser() {
                        public void run() {
                            try {
                                queryMovieOrderById(orderId, timer);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 15000);
                    Thread.sleep(1000 * 60 * 15);//等待15分钟
                    timer.cancel();
                    return "YES";
                }
            });
            executor.execute(future);
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            //线程池必须关闭
            executor.shutdown();
        }
    }

    @Override
    public FilmOrderVO addOrder(String planId, String seatNo, boolean sendMessage, String mobile) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "order_Add");
        map.put("mobile", mobile);
        map.put("plan_id", planId);
        map.put("seat_no", seatNo);
        map.put("send_message", sendMessage ? "true" : "false");
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return jsonObject.getObject("order", FilmOrderVO.class);
    }

    @Override
    public void deleteOrder(String orderId) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "order_Delete");
        map.put("order_id", orderId);
        RequestUtil.baseKouDianYingRequest(map, CONN_URL);
    }

    @Override
    public FilmOrderVO queryOrder(String orderId) throws Exception {
        return template.findOne(new Query(Criteria.where("orderId").is(orderId)), FilmOrderVO.class);
    }
}
