package com.war4.service;

import com.alibaba.fastjson.JSONArray;
import com.war4.base.CutPage;
import com.war4.vo.AMapAdVO;
import com.war4.vo.CityDBVO;
import com.war4.vo.ProvinceDBVO;

import java.util.Collection;
import java.util.List;

/**
 * 地址服务
 * Created by E_Iva on 2018.4.26.0026.
 */
public interface AddressService {
    int saveCityAddress(Collection<AMapAdVO> adds);
    int saveProvinceAddress(Collection<AMapAdVO> adds);
    List<CityDBVO> queryCityByCode(Collection<String> codes);
    List<CityDBVO> queryCity(String keyword);
    JSONArray queryAmapAddress(String address);
    CutPage<CityDBVO> queryAllCity(String provinceCode, String keyword, Integer page, Integer limit);
    CutPage<ProvinceDBVO> queryAllProvince(String keyword, Integer page, Integer limit);
}
