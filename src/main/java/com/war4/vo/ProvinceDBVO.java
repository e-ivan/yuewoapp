package com.war4.vo;

import com.war4.util.ChineseCharUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 数据库省份
 * Created by E_Iva on 2018.4.28.0028.
 */
@Getter@Setter
@Document(collection = "PROVINCE")
public class ProvinceDBVO {
    private String province;
    private String provinceCode;
    private String provincePinYin;
    private String provinceShortPinYin;

    public ProvinceDBVO(String province, String provinceCode) {
        this.province = province;
        this.provinceCode = provinceCode;
        String provinceName = StringUtils.replacePattern(province, "省$", "");
        this.provincePinYin = ChineseCharUtil.changeToTonePinYin(provinceName);
        this.provinceShortPinYin = ChineseCharUtil.changeToGetShortPinYin(provinceName);
    }
}
