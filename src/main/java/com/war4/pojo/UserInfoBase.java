package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.util.BitStateUtil;
import com.war4.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户基本表
 * Created by dly on 2016/7/11.
 */
@Entity
@Table(name = "user_info_base")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserInfoBase {
    public static final int MALE = 1;
    public static final int FEMALE = 0;

    public UserInfoBase() {
    }

    public UserInfoBase(String nickName, String recommendCode, Integer sex, String userPhotoHead) {
        this.nickName = nickName;
        this.recommendCode = recommendCode;
        this.sex = sex;
        this.userPhotoHead = userPhotoHead;
    }

    @Id
    private String id;
    @Column(name = "role_type")
    private String roleType;
    @Column(name = "bit_state")
    private Long bitState = 0L;//用户状态值
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "pay_password")
    private String payPassword;
    @Column(name = "phone")
    private String phone;
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "recommend_code")
    private String recommendCode;
    @Column(name = "sex") //0:女 1:男
    private Integer sex = -1;
    @Column(name = "age")
    private Integer age = 0;
    @Column(name = "intro")
    private String intro;
//    @Column(name = "interest")
//    private String interest;
//    @Column(name = "job")
//    private String job;
    @Column(name = "role")
    private Integer role = 0;
    @Column(name = "constellation")
    private String constellation;

    @Column(name = "birth")
    @DateTimeFormat(pattern = "yyyy年MM月dd日")
    @JsonFormat(pattern = "yyyy年M月dd日",timezone = "GMT+8")
    private Date birth;       //生日

    @Column(name = "x")
    private String x;
    @Column(name = "y")
    private String y;
    @Column(name = "we_open_id")
    private String weOpenId;

    @Column(name = "status")
    private Integer status;
    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    private Date updateTime;                                //更新时间
    @Column(name = "del_flag")
    @JsonIgnore
    private Integer delFlag;                                //删除标记
    @Column(name = "fan_num")
    private Integer fanNum = 0;
    @Column(name = "gift_value")
    private BigDecimal giftValue;
    @Column(name = "internal_flag")
    private Integer internalFlag;                           //内部会员标记
    @Column(name = "token")
    private String token;                                   //融云Token

    @Column(name = "user_photo_head")
    private String userPhotoHead;

    @Column(name = "head_state")
    private Integer headState;

    @Column(name = "activity_id")
    private String activityId;

    @JsonIgnore
    @Column(name = "version")
    @Version
    private Integer version;

    private String deviceToken;

    @Transient
    private  Integer isFriend = 0;//默认为false

    @Transient
    private ChatFriend chatFriend;

    @Transient
    private Integer giftSum = 0;

    @Transient
    private Integer fanSum = 0;

    @Transient
    private List<FileUpload> albumList = new ArrayList<>();

    @Transient
    private UserAccessToken userAccessToken;

    @JsonIgnore
    public String getPassword() {
        return password;
    }


    public BigInteger getAge() {
        return BigInteger.valueOf(DateUtil.getAgeByBirth(birth));
    }

    @JsonIgnore
    public String getPayPassword() {
        return payPassword;
    }

    public boolean getHasAuctionProcess() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_AUCTION_PROCESS);
    }
    public boolean getHasCashProcess() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_CASH_PROCESS);
    }
    public boolean getBindCashAccount() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_BIND_CASH_ACCOUNT);
    }
    public boolean getHasPayPassword() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_PAY_PASSWORD);
    }
    public boolean getHasVideoUpload() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_VIDEO_UPLOAD);
    }
    public boolean getHasAuthenticationSubmit() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_AUTHENTICATION_SUBMIT);
    }
    public boolean getHasAuthenticationPass() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_AUTHENTICATION_PASS);
    }
    public boolean getHasInternetStarPass() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_INTERNETSTAR_PASS);
    }
    public boolean getHasInternetStarReview() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_STAR_REVIEW);
    }
    public boolean getHasVideoChatReview() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_VIDEO_CHAT_REVIEW);
    }
    public boolean getHasCelebrityPass() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_CELEBRITY_PASS);
    }

    public boolean getHasStarPass() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_INTERNETSTAR_PASS) || BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_CELEBRITY_PASS);
    }

    public boolean getHasVideoChatPass() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_VIDEO_CHAT_PASS);
    }

    public boolean getHasOpenVideoChat() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_OPEN_VIDEO_CHAT);
    }

    public boolean getHasVideoChatOnce() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.OP_HAS_VIDEO_CHAT_ONCE);
    }

    public boolean isThirdAdmin() {
        return BitStateUtil.hasState(this.bitState, BitStateUtil.THIRD_ADMIN);
    }

    public void addState(long newState) {
        this.bitState = BitStateUtil.addState(this.bitState, newState);
    }

    public void removeState(long newState) {
        this.bitState = BitStateUtil.removeState(this.bitState, newState);
    }

}
