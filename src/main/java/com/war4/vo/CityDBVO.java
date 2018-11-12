package com.war4.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.war4.util.ChineseCharUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by E_Iva on 2018.4.25.0025.
 */
@Document(collection = "CITY")
@Getter@Setter@ToString
public class CityDBVO {
    private String city;
    @JSONField(name = "adcode")
    private String adCode;
    private String province;
    @JSONField(name = "citycode")
    private String cityCode;
    private String longitude;//经度
    private String latitude;//经度
    private String provinceCode;
    private String cityPinYin;
    private String cityShortPinYin;
    public void setCity(String city){
        this.city = city;
        String cityName = StringUtils.replacePattern(city, "市$", "");
        this.cityPinYin = ChineseCharUtil.changeToTonePinYin(cityName);
        this.cityShortPinYin = ChineseCharUtil.changeToGetShortPinYin(cityName);
    }
    public void setLocation(String location){
        String[] split = StringUtils.split(location, ",");
        if (split.length == 2) {
            this.longitude = split[0];
            this.latitude = split[1];
        }
    }
    public void setAdCode(String adCode){
        this.adCode = adCode;
        String substring = StringUtils.substring(this.adCode, 0, 2);
        if (StringUtils.isNoneBlank(substring)) {
            this.provinceCode = substring + "0000";
        }
    }
}
