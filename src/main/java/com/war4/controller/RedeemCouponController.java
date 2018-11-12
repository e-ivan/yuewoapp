package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.CommonWhether;
import com.war4.enums.CouponRedeemCodeType;
import com.war4.pojo.CouponRedeemCode;
import com.war4.pojo.SystemDictionaryItem;
import com.war4.util.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商城代金券控制器
 * Created by hh on 2017.9.18 0018.
 */
@Controller
@RequestMapping(value = "redeemCoupon")
public class RedeemCouponController extends BaseController{


    //创建验证码
    @RequestMapping(value = "captcha",method = RequestMethod.GET)
    public void captcha(String userId, HttpServletRequest request, HttpServletResponse response){
        try {
            //把校验码转为图像
            BufferedImage image = tempValidCodeService.createCaptcha(userId);
            response.setContentType("image/jpeg");
            //输出图像
            ServletOutputStream outStream = response.getOutputStream();
            ImageIO.write(image, "jpeg", outStream);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建兑换码
     * @param mobile
     * @param split
     */
    @RequestMapping(value = "createRedeemCode",method = RequestMethod.POST)
    @ResponseBody
    public Response createRedeemCode(String mobile,Long split,boolean sendMsg,String userId,String captchaCode,String type){
        //校验验证码
        tempValidCodeService.checkCaptchaCode(userId, captchaCode);
        CouponRedeemCodeType codeType = CouponRedeemCodeType.getByValue(type);
        if (codeType == null) {
            codeType = CouponRedeemCodeType.MARKET;
        }
        String redeemCode = couponRedeemCodeService.createRedeemCode(userId,mobile,split, codeType,sendMsg);
        return new ObjectResponse<>(redeemCode);
    }

    /**
     * 兑换兑换码
     */
    @RequestMapping(value = "exchangeRedeemCode",method = RequestMethod.POST)
    @ResponseBody
    public Response exchangeRedeemCode(String userId,String redeemCode){
        couponRedeemCodeService.exchangeRedeemCode(userId, redeemCode);
        return new Response("兑换成功！");
    }

    /**
     * 兑换码记录列表
     */
    @RequestMapping(value = "queryRedeemCodeList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryRedeemCodeList(String type,Integer state,Integer source,String keyword, String password, Integer page, Integer limit){
        String userId;
        if (StringUtils.isNoneBlank(password)) {//当有密码是查询所有
            super.checkPassword(password);
            userId = null;
        }else {
            userId = UserContext.getUserId();
        }
        CutPage<CouponRedeemCode> cutPage = couponRedeemCodeService.queryRedeemCodeList(type,state,source,userId,keyword,page,limit);
        List<CouponRedeemCode> redeemCodes = cutPage.getiData();
        Set<Long> ids = redeemCodes.stream().map(CouponRedeemCode::getSplitType).collect(Collectors.toSet());
        Map<Long, SystemDictionaryItem> itemMap = baseRepository.queryObjMap(SystemDictionaryItem.class, ids);
        redeemCodes.forEach(r -> {
            SystemDictionaryItem item = itemMap.get(r.getSplitType());
            if (item != null) {
                r.setSplitTypeName(item.getTitle());
            }
        });
        return new ObjectResponse<>(cutPage);
    }

    /**
     * 查询优惠券拆分类型
     */
    @RequestMapping(value = "queryCouponSplit",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCouponSplit(String type){
        StringBuilder sql = new StringBuilder(300);
        sql.append("SELECT i1.* FROM system_dictionary_item i1 LEFT JOIN system_dictionary d ON(i1.parent_id = d.id)")
                .append(" WHERE d.sn = :sn AND i1.status = :status AND i1.field = :field ORDER BY i1.sequence");
        Map<String, Object> map = baseRepository.paramMap();
        map.put("sn","couponSplit");
        map.put("status", CommonWhether.TRUE.getCode());
        map.put("field",type);
        List<SystemDictionaryItem> items = baseRepository.querySqlResult(sql, map, SystemDictionaryItem.class, 0, CutPage.MAX_COUNT);
        return new ObjectResponse<>(items);
    }

}
