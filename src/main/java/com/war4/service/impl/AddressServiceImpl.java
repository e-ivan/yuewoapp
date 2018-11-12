package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.war4.base.BaseService;
import com.war4.base.CutPage;
import com.war4.service.AddressService;
import com.war4.util.RequestUtil;
import com.war4.vo.AMapAdVO;
import com.war4.vo.CityDBVO;
import com.war4.vo.ProvinceDBVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by E_Iva on 2018.4.26.0026.
 */
@Service
public class AddressServiceImpl extends BaseService implements AddressService {

    @Override
    public int saveCityAddress(Collection<AMapAdVO> adds) {
        Pattern pattern = Pattern.compile(".*[0]{2}$");
        List<AMapAdVO> citys = adds.stream().filter(i -> pattern.matcher(i.getAdCode()).matches() && StringUtils.isNoneBlank(i.getCityCode())).distinct().collect(Collectors.toList());
        int size = citys.size();
        int i = size % 10 == 0 ? size / 10 : size / 10 + 1;
        List<CityDBVO> gList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            List<AMapAdVO> collect = citys.stream().skip(j * 10).limit(10).collect(Collectors.toList());
            //查询城市地址信息
            List<String> names = collect.stream().map(AMapAdVO::getName).collect(Collectors.toList());
            String cityJoin = StringUtils.join(names, "|");
            List<CityDBVO> cityDB = JSON.parseArray(this.queryAmapAddress(cityJoin).toJSONString(), CityDBVO.class);
            for (int k = 0; k < collect.size(); k++) {
                cityDB.get(k).setCityCode(collect.get(k).getCityCode());
                cityDB.get(k).setCity(collect.get(k).getName());
            }
            gList.addAll(cityDB);
        }
        //只保存新数据
        List<String> cityCodes = gList.stream().map(CityDBVO::getCityCode).distinct().collect(Collectors.toList());
        List<CityDBVO> cityDBs = this.queryCityByCode(cityCodes);
        List<CityDBVO> newCitys = gList.stream().filter(c -> cityDBs.stream().noneMatch(t -> t.getCityCode().equals(c.getCityCode()))).collect(Collectors.toList());
        template.insert(newCitys, CityDBVO.class);
        return newCitys.size();
    }

    @Override
    public int saveProvinceAddress(Collection<AMapAdVO> adds) {
        Pattern pattern = Pattern.compile("\\d{2}[0]{4}$");//获取省份正则
        List<AMapAdVO> provinces = adds.stream().filter(a -> pattern.matcher(a.getAdCode()).matches()).collect(Collectors.toList());
        List<String> provinceCodes = provinces.stream().map(AMapAdVO::getAdCode).collect(Collectors.toList());
        List<ProvinceDBVO> provinceDBs = template.find(new Query(Criteria.where("provinceCode").in(provinceCodes)), ProvinceDBVO.class);
        List<ProvinceDBVO> newProvinces = provinces.stream().filter(p -> provinceDBs.stream().noneMatch(t -> t.getProvinceCode().equals(p.getAdCode())))
                .map(a -> new ProvinceDBVO(a.getName(), a.getAdCode())).collect(Collectors.toList());
        template.insert(newProvinces,ProvinceDBVO.class);
        return newProvinces.size();
    }

    @Override
    public List<CityDBVO> queryCityByCode(Collection<String> codes) {
        return template.find(new Query(Criteria.where("cityCode").in(codes)), CityDBVO.class);
    }

    @Override
    public List<CityDBVO> queryCity(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return template.findAll(CityDBVO.class);
        }
        Criteria criteria = new Criteria();
        Pattern pattern = Pattern.compile(".*" + keyword + ".*");
        criteria.orOperator(Criteria.where("city").regex(pattern), Criteria.where("cityPinYin").is(keyword), Criteria.where("cityShortPinYin").is(keyword));
        return template.find(new Query(criteria), CityDBVO.class);
    }


    @Override
    public JSONArray queryAmapAddress(String address) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        if (StringUtils.contains(address, "|")) {
            map.put("batch", "true");
        }
        JSONObject jsonObject = RequestUtil.baseAmapRequest(map, "/geocode/geo");
        return JSON.parseArray(jsonObject.getString("geocodes"));
    }

    @Override
    public CutPage<CityDBVO> queryAllCity(String provinceCode, String keyword, Integer page, Integer limit) {
        CutPage<CityDBVO> cutPage = new CutPage<>(page, limit);
        Query query = new Query();
        if (StringUtils.isNoneBlank(provinceCode)) {
            query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (StringUtils.isNoneBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("city").regex(pattern), Criteria.where("cityPinYin").regex(pattern),Criteria.where("cityShortPinYin").regex(pattern));
            query.addCriteria(criteria);
        }

        long count = template.count(query, CityDBVO.class);
        cutPage.setTotalCount(count);
        List<CityDBVO> citys = template.find(query.skip(cutPage.getOffset()).limit(cutPage.getLimit()), CityDBVO.class);
        cutPage.setiData(citys);
        return cutPage;
    }

    @Override
    public CutPage<ProvinceDBVO> queryAllProvince(String keyword, Integer page, Integer limit) {
        CutPage<ProvinceDBVO> cutPage = new CutPage<>(page, limit);
        Query query = new Query();
        if (StringUtils.isNoneBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("province").regex(pattern), Criteria.where("provincePinYin").regex(pattern),Criteria.where("provinceShortPinYin").regex(pattern));
            query.addCriteria(criteria);
        }
        long count = template.count(query, ProvinceDBVO.class);
        cutPage.setTotalCount(count);
        List<ProvinceDBVO> provinces = template.find(query.skip(cutPage.getOffset()).limit(cutPage.getLimit()), ProvinceDBVO.class);
        cutPage.setiData(provinces);
        return cutPage;
    }


}
