package com.war4.vo;

import com.war4.base.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * 用户简单信息
 * Created by E_Iva on 2018.3.23.0023.
 */
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class SimpleUserVO extends BaseVO {
    protected String userId;
    protected String headImg;     //用户头像
    protected BigInteger age;
    protected Integer sex;
    protected String nickname;
    protected Double distance;
}
