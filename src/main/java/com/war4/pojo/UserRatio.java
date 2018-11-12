package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.base.PropertiesConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户占比
 * Created by E_Iva on 2018.1.22.0022.
 */
@Entity
@Table(name = "user_ratio")
@Getter@Setter@NoArgsConstructor
public class UserRatio {
    public UserRatio(String userId, BigDecimal videoChatRatio, BigDecimal auctionRatio) {
        this.userId = userId;
        this.videoChatRatio = videoChatRatio;
        this.auctionRatio = auctionRatio;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private String userId;
    private BigDecimal videoChatRatio;
    private BigDecimal auctionRatio;
    @JsonIgnore
    private Timestamp created;

    public Map<String,BigDecimal> getVideoChatRule(){
        Map<String,BigDecimal> map = new HashMap<>();
        map.put("times", PropertiesConfig.getVideoChatCostProportion()[2]);
        map.put("start", PropertiesConfig.getVideoChatCostProportion()[0]);
        map.put("later", PropertiesConfig.getVideoChatCostProportion()[1]);
        return map;
    }

    public Map<String,BigDecimal> getAuctionRule(){
        Map<String,BigDecimal> map = new HashMap<>();
        map.put("times", PropertiesConfig.getAuctionRewardProportion()[2]);
        map.put("start", PropertiesConfig.getAuctionRewardProportion()[0]);
        map.put("later", PropertiesConfig.getAuctionRewardProportion()[1]);
        return map;
    }
}
