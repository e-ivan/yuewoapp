package com.war4.service;

import com.war4.pojo.EvaluateDetailed;

import java.util.List;

/**
 * 评价系统
 * Created by hh on 2017.11.3 0003.
 */
public interface EvaluateSystemService {

    //创建评价清单
    void createEvaluateDetailed(EvaluateDetailed detailed);

    //设置订单为已评价
    void setOrderEvaluate(String orderId);

    /**
     * @param auto  自动评价
     */
    void setOrderEvaluate(String orderId,boolean auto);

    //查询订单评价清单
    List<EvaluateDetailed> queryOrderEvaluateDetailed(String orderId);

}
