package com.war4.pojo;

import com.war4.vo.SimpleUserVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 附近有约
 * Created by E_Iva on 2018.3.23.0023.
 */
@Entity
@Table(name = "nearby_dating")
@DynamicInsert()
@DynamicUpdate()
@Getter
@Setter
public class NearbyDating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String movieName;
    private String pic;
    private String site;
    private String lng; //经度
    private String lat; //纬度
    private Date datingDate;
    private Integer sex;
    private String content;
    private Integer state;
    private Integer paySite;
    private Timestamp created;
    private Integer applyCount;
    private Integer commentCount;
    private Integer viewCount;

    @Transient
    private SimpleUserVO user;
    @Transient
    private boolean joinState;
    @Transient
    private List<NearbyDatingApplyItem> applyItems;
}
