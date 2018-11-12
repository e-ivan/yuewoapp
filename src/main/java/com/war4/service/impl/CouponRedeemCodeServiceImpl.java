package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.CouponRedeemCodeType;
import com.war4.enums.MarketCouponSource;
import com.war4.enums.UserRoleType;
import com.war4.listener.event.RedeemCodeEvent;
import com.war4.pojo.CouponRedeemCode;
import com.war4.pojo.SystemDictionaryItem;
import com.war4.pojo.UserCorrelative;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.NumberUtil;
import com.war4.util.ShareCodeUtil;
import com.war4.vo.FilmCouponExpressionVO;
import com.war4.vo.MarketCouponExpressionVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hh on 2017.9.18 0018.
 */
@Service
public class CouponRedeemCodeServiceImpl extends BaseService implements CouponRedeemCodeService {
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private SystemDictionaryService systemDictionaryService;
    @Autowired
    private MarketCouponService marketCouponService;
    @Autowired
    private UserCorrelativeService correlativeService;
    @Autowired
    private UserCouponService userCouponService;


    @Override
    @Transactional
    public String createRedeemCode(String userId, String mobile, Long spilt, CouponRedeemCodeType type, boolean sendMsg) {
        if (!NumberUtil.isNumeric(mobile)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "手机号码格式错误");
        }
        SystemDictionaryItem item = systemDictionaryService.getSystemDictionaryItemById(1, spilt);
        if (item == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "类型异常，请联系技术人员！");
        }
        boolean flag = false;
        switch (type) {
            case MARKET:
                flag = StringUtils.equals(item.getField(), "market");
                break;
            case FILM:
                flag = StringUtils.equals(item.getField(), "film");
                break;
        }
        if (!flag) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "拆封类型不匹配");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null || !UserRoleType.SUPERMANAGER.getCode().equals(user.getRoleType())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户异常，请联系技术人员！");
        }
        //生成兑换码
        String redeemCode = createRedeemCode();
        //创建兑换码对象
        CouponRedeemCode crc = new CouponRedeemCode();
        crc.setSource(MarketCouponSource.CINEMA_RECHARGE.getCode());
        crc.setAdminId(userId);
        crc.setMobile(mobile);
        crc.setSplitType(spilt);
        crc.setRedeemCode(redeemCode);
        crc.setType(type.getCode());
        baseRepository.saveObj(crc);
        if (sendMsg) {
            ac.publishEvent(new RedeemCodeEvent(crc));
        }
        return redeemCode;
    }

    @Override
    @Transactional
    public void exchangeRedeemCode(String userId, String redeemCode) {
        //校验信息
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "用户不存在！");
        }
        CouponRedeemCode crc = getByRedeemCode(redeemCode);
        if (crc == null || crc.isUse()) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "兑换码无效！");
        }
        SystemDictionaryItem item = systemDictionaryService.getSystemDictionaryItemById(1, crc.getSplitType());
        //商城优惠券
        if (CouponRedeemCodeType.MARKET.getCode().equals(crc.getType())) {
            int count = 0;
            //封装规则
            MarketCouponExpressionVO mce = JSON.parseObject(item.getExpression(), MarketCouponExpressionVO.class);
            //创建代金券
            for (MarketCouponExpressionVO.Coupon coupon : mce.getItem()) {
                for (int i = 0; i < coupon.getCount(); i++) {
                    marketCouponService.createMarketCoupon(userId, new BigDecimal(coupon.getMoney()), item.getTitle(), mce.getValid(), new BigDecimal(coupon.getMinUse()), MarketCouponSource.CINEMA_RECHARGE);
                    count++;
                }
            }
            //用户商城优惠券数量增加
            correlativeService.addCount(UserCorrelative.MARKET_COUPONS, userId, count);
        } else if (CouponRedeemCodeType.FILM.getCode().equals(crc.getType())) {//电影优惠券
            int count = 0;
            List<FilmCouponExpressionVO> fce = JSON.parseArray(item.getExpression(), FilmCouponExpressionVO.class);
            for (FilmCouponExpressionVO f : fce) {
                for (int i = 0; i < f.getCount(); i++) {
                    userCouponService.addUserCoupon(userId, f.getCouponId(), count);
                    count++;
                }
            }
            correlativeService.addCount(UserCorrelative.FILM_COUPONS, userId, count);
        }
        crc.setUse(true);
        crc.setUseTime(new Date());
        crc.setUserId(userId);
        baseRepository.updateObj(crc);
    }

    @Override
    public CouponRedeemCode getByRedeemCode(String redeemCode) {
        String hql = "from CouponRedeemCode where redeemCode = :redeemCode";
        Map<String, Object> map = new HashMap<>();
        map.put("redeemCode", redeemCode);
        return baseRepository.executeHql(hql, map);
    }

    private String createRedeemCode() {
        String redeemCode = ShareCodeUtil.getCodeByLong(6, true);
        //查询是否重复
        if (getByRedeemCode(redeemCode) != null) {
            return createRedeemCode();
        }
        return redeemCode;
    }

    @Override
    public CutPage<CouponRedeemCode> queryRedeemCodeList(String type, Integer state, Integer source, String userId, String keyword, Integer page, Integer limit) {

        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();
        hql.append("from CouponRedeemCode where type = :type ");
        CouponRedeemCodeType codeType = CouponRedeemCodeType.getByValue(type);
        map.put("type", codeType != null ? codeType.getCode() : -1);
        if (StringUtils.isNoneBlank(userId)) {
            hql.append(" and adminId = :adminId ");
            map.put("adminId", userId);
        }
        if (state != null) {
            hql.append(" and isUse = :use ");
            map.put("use",state.equals(1));
        }
        if (StringUtils.isNotBlank(keyword)) {
            hql.append("and (mobile like :keyword or redeemCode like :keyword or userId like :keyword or adminId like :keyword) ");
            map.put("keyword", "%" + keyword + "%");
        }
        if (source != null) {
            hql.append(" and source = :source");
            map.put("source",source);
        }
        hql.append(" order by created desc");
        return baseRepository.executeHql(hql.toString(), map, page, limit);
    }

}
