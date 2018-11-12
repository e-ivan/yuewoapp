package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/12/2.
 */
@Getter
@AllArgsConstructor
public enum WechatParams {
    WECHAT_PARAMS_TEACHER("goods","wx111cbbfff3f19ea7","0854929c233d7ff52f09818fa009d1ea","1450795802","r9Hl003Mp0j12Ef88rA36j6QOvCzChKj") ;


    private String clientType;
    private String appId;
    private String appSecret;
    private String merchantId;
    private String apiKey;

    public static WechatParams getWechatParamsByClientType(String clientType){
            for(WechatParams wechatParams: WechatParams.values()){
                if (wechatParams.getClientType().equals(clientType)){
                    return wechatParams;
                }
            }
            return null;
        }
}
