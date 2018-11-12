package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.AdPage;
import com.war4.vo.AdPageParamVO;

import java.util.List;

/**
 * 宣传页面服务
 * Created by E_Iva on 2018.3.29.0029.
 */
public interface AdPageService {
    void createAdPage(AdPageParamVO vo);
    List<AdPage> queryAdPage(Integer type);
    CutPage<AdPage> queryAllAdPage(Integer state,Integer type,Integer page,Integer limit);
    void updateAdPageState(Long adId,boolean state);
}
