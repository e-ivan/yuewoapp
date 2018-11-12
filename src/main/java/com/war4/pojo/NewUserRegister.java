package com.war4.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 新用户注册表
 * Created by hh on 2017.10.11 0011.
 */
@Entity
@Table(name = "new_user_register")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter@NoArgsConstructor
public class NewUserRegister {

    public static final byte CREATE = 0;
    public static final byte DONE = 1;
    public NewUserRegister(String userId) {
        this.userId = userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Timestamp created;

    private byte state;

    private Date updated;

    private String remark;

}
