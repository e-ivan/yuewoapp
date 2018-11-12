package com.war4.pojo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.enums.AdPageTypeVar;
import com.war4.util.BitStateUtil;
import com.war4.vo.AdPageImageNameVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 宣传页面
 * Created by E_Iva on 2018.3.29.0029.
 */
@Entity
@Table(name = "ad_page")
@DynamicInsert()
@DynamicUpdate()
@Getter@Setter
public class AdPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @JsonIgnore
    private String images;

    private Integer type = 0;

    private Integer location;

    private String value;

    private Integer state;

    private Date showStartTime;

    private Date showEndTime;

    private Timestamp created;

    public AdPageImageNameVO getImage(){
        return JSON.parseObject(this.images, AdPageImageNameVO.class);
    }

    public void addType(AdPageTypeVar newType) {
        if (newType != null) {
            this.type = (int) BitStateUtil.addState(this.type, newType.getTypeValue());
        }
    }

    public boolean isHomeBanner(){
        return BitStateUtil.hasState(this.type, AdPageTypeVar.AD_HOME_BANNER.getTypeValue());
    }

    public boolean isHomePop(){
        return BitStateUtil.hasState(this.type, AdPageTypeVar.AD_HOME_POP.getTypeValue());
    }

    public boolean isAuctionBanner(){
        return BitStateUtil.hasState(this.type, AdPageTypeVar.AD_AUCTION_BANNER.getTypeValue());
    }

    public void removeType(AdPageTypeVar newType) {
        if (newType != null) {
            this.type = (int)BitStateUtil.removeState(this.type, newType.getTypeValue());
        }
    }

}
