package com.war4.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户简单数据
 * Created by E_Iva on 2018.1.11.0011.
 */
@Getter@Setter@AllArgsConstructor
public class UserSimpleInfoVO {
    private String id;
    private String nickName;
    private String token;
    private String userPhotoHead;
}
