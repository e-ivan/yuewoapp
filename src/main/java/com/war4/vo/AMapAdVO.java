package com.war4.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by E_Iva on 2018.4.25.0025.
 */
@Getter@Setter@AllArgsConstructor
public class AMapAdVO {
    private String name;
    private String adCode;
    private String cityCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AMapAdVO aMapAdVO = (AMapAdVO) o;

        return cityCode != null ? cityCode.equals(aMapAdVO.cityCode) : aMapAdVO.cityCode == null;
    }

    @Override
    public int hashCode() {
        return cityCode != null ? cityCode.hashCode() : 0;
    }
}
