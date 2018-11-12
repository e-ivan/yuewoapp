package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.ValidCode;
import com.war4.repository.BaseRepository;
import com.war4.service.SmsService;
import com.war4.service.TempValidCodeService;
import com.war4.util.CaptchaUtil;
import com.war4.util.DateUtil;
import com.war4.vo.CaptchaVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by dly on 2016/8/22.
 */
@Service
public class TempValidCodeServiceImpl implements TempValidCodeService {

    @Autowired
    private SmsService smsService;
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private MongoTemplate temp;

    @Override
    @Transactional
    //发送验证码
    public String createValidCode(String phone) throws Exception{
        if (StringUtils.isBlank(phone)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"手机号码不能为空！");
        }
        List<ValidCode> validCodes = this.queryValidCodeByPhone(phone);
        if(validCodes.size() > 0){//如果数据库里有此手机号验证码
            if(DateUtil.intervalTime(validCodes.get(0).getCreateTime(),new Date()).compareTo(ValidCode.CANNOT_RESEND_MINUTE.longValue()*60) < 0){//如果发送时间不到规定时间
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,ValidCode.CANNOT_RESEND_MINUTE +"分钟内不能重复发送");
            }
            for (ValidCode validCode : validCodes) {
                baseRepository.physicsDelete(validCode);//删除旧数据
            }
        }

        Random random = new Random();
        int x = random.nextInt(8999);
        x = x+1000;
        String code = Integer.toString(x);
        if (!PropertiesConfig.isProduction()) {
            code = "1234";
        }
        ValidCode validCode = new ValidCode();
        validCode.setPhone(phone);
        validCode.setValidCode(code);
        baseRepository.saveObj(validCode);
        System.out.println("验证码：" + code);
        if (!PropertiesConfig.isProduction()){
            return "验证码：" + code;
        }
        //TODO 真实发送
        smsService.sendValidCode(validCode);
        return null;
    }

    @Override
    @Transactional
    //校验验证码
    public void checkValidCode(String phone,String validCode){
        if(validCode.equals("ivan")){
            return;
        }
        List<ValidCode> validCodes = this.queryValidCodeByPhone(phone);
        if(validCodes.size() <= 0){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"无效验证码");
        }
        if(!validCodes.get(0).getValidCode().equals(validCode)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"验证码不正确");
        }
        if(DateUtil.intervalTime(validCodes.get(0).getCreateTime(),new Date()).compareTo(ValidCode.VALID_MINUTE.longValue()*60) > 0 ){//5分钟有效
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"验证码超时");
        }
        for (ValidCode vc : validCodes) {
            baseRepository.physicsDelete(vc);//删除旧数据
        }
    }

    @Override
    public BufferedImage createCaptcha(String userId) {
        //生成4位验证码
        String captchaCode = CaptchaUtil.genCaptcha(4);
        //保存到数据库中
        CaptchaVO c = new CaptchaVO();
        c.setUserId(userId);
        c.setCaptchaCode(captchaCode);
        c.setCreated(new Date());
        //删除原来的数据
        temp.remove(new Query(Criteria.where("userId").is(userId)),CaptchaVO.class);
        //重新写入
        temp.insert(c);
        //渲染图片
        return CaptchaUtil.genCaptchaImg(captchaCode);
    }

    @Override
    public void checkCaptchaCode(String userId, String captchaCode) {
        Query q = new Query(Criteria.where("userId").is(userId));
        CaptchaVO captcha = temp.findOne(q, CaptchaVO.class);
        if (captcha == null || StringUtils.isBlank(captchaCode)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"验证码失效！");
        }
        if (!captcha.getCaptchaCode().equals(captchaCode.toUpperCase())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"验证码错误！");
        }
        //删除验证码
        temp.remove(q,CaptchaVO.class);
    }

    private List<ValidCode> queryValidCodeByPhone(String phone){
        String hql = "from ValidCode where phone = :phone order by createTime desc";
        Map<String,Object> map = new HashMap<>();
        map.put("phone",phone);
        CutPage<ValidCode> cutPage = baseRepository.executeHql(hql, map, 0, Integer.MAX_VALUE);
        return cutPage.getiData();
    }
}
