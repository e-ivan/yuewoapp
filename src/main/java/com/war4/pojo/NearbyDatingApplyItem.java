package com.war4.pojo;

import com.war4.vo.SimpleUserVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 *  附近约会报名明细
 * Created by E_Iva on 2018.3.23.0023.
 */
@Entity
@Table(name = "nearby_dating_apply_item")
@DynamicInsert()
@DynamicUpdate()
@Getter
@Setter
public class NearbyDatingApplyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nId;
    private String userId;
    private Timestamp created;
    @Transient
    private SimpleUserVO user;

}
