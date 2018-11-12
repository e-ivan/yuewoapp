package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 投标活动明细
 * Created by hh on 2017.10.17 0017.
 */
@Entity
@Table(name = "activity_bid_item")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class ActivityBidItem implements Cloneable {

    public static final Byte CREATE = 0;    //创建
    public static final Byte VICTOR = 1;    //中奖
    public static final Byte WAIT = 2;      //等待开奖
    public static final Byte FINISH = 3;    //结束
    public static final Byte NEXT = 4;      //下一场
    public static final Byte CANCEL = 5;    //取消

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String headImg;

    private String nickname;

    private Long bidId;

    private Integer joinCount;

    private Integer currentCount;

    private Timestamp created;

    private BigDecimal price;

    private Date startTime;

    private Byte state;

    private String orderId;

    private BigDecimal payMoney;

    private Date payTime;

    private String outTradeNo;

    private String payType;

    @Version
    private Integer version;

    @Override
    public ActivityBidItem clone()  {
        try {
            return (ActivityBidItem)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
