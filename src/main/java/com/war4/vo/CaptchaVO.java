package com.war4.vo;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 图片验证码临时对象
 * Created by hh on 2017.9.18 0018.
 */
@Document(collection = "CAPTCHA")
@Getter
@Setter
public class CaptchaVO extends BaseVO {
    private static final long serialVersionUID = -4786828093197846189L;
    private String userId;
    private String captchaCode;
    private Date created;
}
