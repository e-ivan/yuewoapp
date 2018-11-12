package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.DatingPayType;
import com.war4.enums.UserCouponStatus;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.DatingFilmService;
import com.war4.service.UserCorrelativeService;
import com.war4.service.UserOtherCouponService;
import com.war4.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/1/17.
 */
@Service
public class UserOtherCouponServiceImpl extends BaseService implements UserOtherCouponService {


    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserCorrelativeService correlativeService;

    @Autowired
    DatingFilmService datingFilmService;


    @Override
    public CutPage<UserOtherCoupon> queryAllDatingUserOtherCouponByUserId(String userId, String[] couponIds) {

        StringBuilder hql = new StringBuilder();

        hql.append("from UserOtherCoupon where fkUserId = '" + userId + "' ");

        hql.append("and ( ");
        for (int i = 0; i < couponIds.length; i++) {

            hql.append("fkCouponId = '" + couponIds[i] + "' ");

            if (i != (couponIds.length - 1)) {
                hql.append(" or ");
            }
        }
        hql.append(") order by createTime desc");

        CutPage<UserOtherCoupon> userCouponCutPage = baseRepository.executeHql(hql.toString(), null, 0, Integer.MAX_VALUE);

        return userCouponCutPage;
    }


    @Override
    @Transactional
    public void addDatingUserCoupon(String fkUserId, String datingId) {
        if (datingId == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "datingId不能为空！");
        }
        if (fkUserId == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "userId不能为空！");
        }
        DatingFilm datingFilm = baseRepository.getObjById(DatingFilm.class, datingId);
        if (datingFilm == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该约会！");
        }

        //找到符合资格的优惠券id
        String couponIdsStr2 = PropertiesConfig.getDatingGetFilmsCouponId();
        String[] couponIds = couponIdsStr2.split(",");

        //防止刷优惠券，检查是否有资格获取优惠券
        CutPage<UserOtherCoupon> userCouponCutPage = this.queryAllDatingUserOtherCouponByUserId(fkUserId, couponIds);

        if (userCouponCutPage.getiData().size() != 0) {

            UserOtherCoupon _userCoupon = userCouponCutPage.getiData().get(0);

            Date createTime = _userCoupon.getCreateTime();//得到创建时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String creatStr = formatter.format(createTime);
            Date dateTemp_createTime = new Date();
            try {
                dateTemp_createTime = formatter.parse(creatStr);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            //判断是否今天赠送过
            if (DateUtil.isNow(dateTemp_createTime, "yyyy-MM-dd")) {
                throw new BusinessException(CommonErrorResult.SECRET_FAIL, "当天内已获得优惠券！");
            }

        }


        String couponId = UUID.randomUUID().toString();

        UserOtherCoupon userOtherCoupon = new UserOtherCoupon();
        userOtherCoupon.setId(couponId);
        int payType = datingFilm.getPayType();
        Coupon coupon;// = baseRepository.getObjById(Coupon.class, couponId);
        if (payType == DatingPayType.AA.getCode()) {
            userOtherCoupon.setFkCouponId(couponIds[0]);
            coupon = baseRepository.getObjById(Coupon.class, couponIds[0]);
        } else {
            userOtherCoupon.setFkCouponId(couponIds[1]);
            coupon = baseRepository.getObjById(Coupon.class, couponIds[1]);
        }
        userOtherCoupon.setType(1);//表示优惠券类型是约会电影送的
        userOtherCoupon.setFkUserId(fkUserId);
        //设置日期
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, coupon.getPrescription());
        userOtherCoupon.setOffTime(calendar.getTime());
        userOtherCoupon.setObjectId(datingId);
        baseRepository.saveObj(userOtherCoupon);


        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setId(couponId);

        Coupon coupon2;// = baseRepository.getObjById(Coupon.class, couponId);
        if (payType == DatingPayType.AA.getCode()) {
            userCoupon.setFkCouponId(couponIds[0]);
            coupon2 = baseRepository.getObjById(Coupon.class, couponIds[0]);
        } else {
            userCoupon.setFkCouponId(couponIds[1]);
            coupon2 = baseRepository.getObjById(Coupon.class, couponIds[1]);
        }

        userCoupon.setFkUserId(fkUserId);
        userCoupon.setIsUse(UserCouponStatus.NOT_USE.getCode());
        //设置日期
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, coupon2.getPrescription());
        userCoupon.setOffTime(calendar.getTime());
        baseRepository.saveObj(userCoupon);

        //用户优惠券+1
        correlativeService.addCount(UserCorrelative.FILM_COUPONS, fkUserId);
    }

}
