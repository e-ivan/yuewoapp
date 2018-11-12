package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.Coupon;
import com.war4.pojo.UserCoupon;
import com.war4.repository.BaseRepository;
import com.war4.service.CouponService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017/1/17.
 */
@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public Coupon addCoupon(Coupon coupon) {
        if (StringUtils.isBlank(coupon.getName())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券名称不能为空");
        }
        if (coupon.getPrescription() == null || coupon.getPrescription() <= 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券时效不正确");
        }
        if (coupon.getType() == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "请选择优惠券类型");
        }
        BigDecimal money = coupon.getMoney();
        if (Objects.equals(coupon.getType(),Coupon.NORMAL) && (money == null || money.compareTo(BigDecimal.ZERO) <= 0)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券金额不正确");
        }
        Integer seats = coupon.getSeats();
        if (Objects.equals(coupon.getType(),Coupon.COMMON) && (seats == null || seats <= 0)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券座位数不正确");
        }
        Coupon c = new Coupon();
        c.setId(UUID.randomUUID().toString());
        c.setName(coupon.getName());
        c.setType(coupon.getType());
        c.setMoney(Objects.equals(coupon.getType(),Coupon.NORMAL) ? money : new BigDecimal("0"));
        c.setSeats(Objects.equals(coupon.getType(),Coupon.COMMON) ? seats : 1);
        if (StringUtils.isNotBlank(coupon.getMovieId())) {
            c.setMovieId(coupon.getMovieId());
            c.setPic(coupon.getPic());
        }
        c.setPrescription(coupon.getPrescription());
        baseRepository.saveObj(c);
        return c;
    }

    @Override
    @Transactional
    public Coupon updateCoupon(Coupon coupon) {
        if (StringUtils.isBlank(coupon.getName())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券名称不能为空");
        }
        if (coupon.getPrescription() == null || coupon.getPrescription() <= 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券时效不正确");
        }
        if (coupon.getType() == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "请选择优惠券类型");
        }
        BigDecimal money = coupon.getMoney();
        if (Objects.equals(coupon.getType(),Coupon.NORMAL) && (money == null || money.compareTo(BigDecimal.ZERO) <= 0)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券金额不正确");
        }
        Integer seats = coupon.getSeats();
        if (Objects.equals(coupon.getType(),Coupon.COMMON) && (seats == null || seats <= 0)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券座位数不正确");
        }
        Coupon c = baseRepository.getObjById(Coupon.class, coupon.getId());
        c.setName(coupon.getName());
        c.setType(coupon.getType());
        c.setMoney(Objects.equals(coupon.getType(),Coupon.NORMAL) ? money : new BigDecimal("0"));
        c.setSeats(Objects.equals(coupon.getType(),Coupon.COMMON) ? seats : 1);
        if (StringUtils.isNotBlank(coupon.getMovieId())) {
            c.setMovieId(coupon.getMovieId());
            c.setPic(coupon.getPic());
        }else {
            c.setMovieId(null);
            c.setPic(null);
        }
        c.setPrescription(coupon.getPrescription());
        baseRepository.updateObj(c);
        return coupon;
    }

    @Override
    public Coupon getCoupon(String couponId) {
        return baseRepository.getObjById(Coupon.class,couponId);
    }

    @Override
    @Transactional
    public void delCoupon(String couponId) {
        Coupon coupon = baseRepository.getObjById(Coupon.class, couponId);
        if (coupon != null) {
            baseRepository.logicDelete(coupon);
        }
    }

    @Override
    public void restoreCoupon(String couponId) {
        Coupon coupon = baseRepository.getObjById(Coupon.class, couponId);
        if (coupon != null) {
            coupon.setDelFlag(0);
            baseRepository.updateObj(coupon);
        }
    }

    @Override
    public CutPage<Coupon> queryCoupons(Integer delFlag, String keyword, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        Map<String, Object> paramMap = baseRepository.paramMap();
        hql.append("from Coupon where delFlag = :delFlag ");
        if (StringUtils.isNoneBlank(keyword)) {
            hql.append(" and name like :keyword ");
            paramMap.put("keyword","%" + keyword + "%");
        }
        hql.append("order by createTime desc");
        paramMap.put("delFlag",delFlag);
        return baseRepository.executeHql(hql,paramMap,page,limit);
    }

    @Override
    public List<Integer> queryCouponMoneys(String[] couponIds) {

        List<Integer> list = new ArrayList<>();

        for (String cid : couponIds) {

            String hql = "from Coupon where id='" + cid + "'";
            Coupon coupon = baseRepository.queryUniqueData(hql);
            Integer money = coupon.getMoney().intValue();
            list.add(money);
        }

        return list;

    }

    @Override
    @Transactional
    public void deleteCouponAndAllUserCoupon(String couponId) {
        Coupon coupon = baseRepository.getObjById(Coupon.class, couponId);
        if (coupon == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "优惠券类型不存在！");
        }
        String hql = "from UserCoupon where fkCouponId = :couponId ";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("couponId",couponId);
        List<UserCoupon> result = baseRepository.queryHqlResult(hql, paramMap, 0, CutPage.MAX_COUNT);
        if (result.stream().anyMatch(uc -> uc.getIsUse() == 1)) {
            //有人使用，讲优惠券设置为其他状态
            coupon.setDelFlag(3);
            baseRepository.updateObj(coupon);
        }else {
            //没有人使用，直接删除优惠券
            baseRepository.physicsDelete(coupon);
        }
        //删除没有使用的优惠券
        hql = "delete from UserCoupon where fkCouponId = :couponId and isUse = :use";
        paramMap.put("use", 0);
        baseRepository.executeHql(hql, paramMap);
    }
}
