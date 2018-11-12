package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.MarketCouponSource;
import com.war4.pojo.MarketCoupon;
import com.war4.repository.BaseRepository;
import com.war4.service.MarketCouponService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hh on 2017.9.15 0015.
 */
@Service
public class MarketCouponServiceImpl implements MarketCouponService{

    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public MarketCoupon createMarketCoupon(String userId,BigDecimal money ,String name,int valid, BigDecimal minUse, MarketCouponSource source) {
        MarketCoupon mc = new MarketCoupon();
        mc.setCouponId(UUID.randomUUID().toString());
        mc.setUserId(userId);
        mc.setMoney(money);
        mc.setName(name);
        mc.setMinUse(minUse);
        mc.setValid(valid);
        mc.setOffTime(DateUtils.addDays(new Date(),valid));
        mc.setSource(source.getCode());
        baseRepository.saveObj(mc);
        return mc;
    }

    @Override
    public CutPage<MarketCoupon> queryMarketCoupon(String userId, BigDecimal price, boolean ifPast, Integer source, String orderBy, Integer page, Integer limit) {
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空！");
        }
        StringBuilder hql = new StringBuilder(200);
        Map<String,Object> map = new HashMap<>();
        hql.append("from MarketCoupon where userId = :userId ");
        map.put("userId",userId);
        if (ifPast){//可使用
            hql.append(" and (isUse = 1 or offTime <= :now)");
        }else {
            hql.append(" and isUse = 0 and offTime >= :now");
        }
        map.put("now",new Date());
        if (source != null){
            hql.append(" and source = :source ");
            map.put("source",source);
        }
        if (StringUtils.isNotBlank(orderBy) && orderBy.equals("optimal")){
            hql.append(" and minUse < :price");
        }
        hql.append(" order by ");
        switch (orderBy == null ? "" : orderBy){
            case "offTime" : hql.append(" offTime desc");break;
            case "optimal" : hql.append(" elt(interval(minUse,0,:price), 1,2),money desc,minUse asc,offTime asc");break;
            case "minUse" : hql.append(" elt(interval(minUse,0,:price), 1,2), minUse desc");break;
            default:hql.append("money");
        }
        map.put("price",price);
        return baseRepository.executeHql(hql.toString(),map,page,limit);
    }

    @Override
    public MarketCoupon getMarketCoupon(String couponId) {
        String hql = "from MarketCoupon where couponId = :couponId";
        Map<String,Object> map = new HashMap<>();
        map.put("couponId",couponId);
        return baseRepository.executeHql(hql,map);
    }

    @Override
    public void updateMarketCoupon(String couponId, boolean use) {
        MarketCoupon marketCoupon = getMarketCoupon(couponId);
        if (marketCoupon.isUse() == use) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,use ? "已为使用!" : "已为未使用");
        }
        marketCoupon.setUse(use);
        marketCoupon.setUseTime(use ? new Date() : null);
        baseRepository.updateObj(marketCoupon);
    }

}
