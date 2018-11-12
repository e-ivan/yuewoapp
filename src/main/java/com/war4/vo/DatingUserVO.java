package com.war4.vo;

import com.war4.pojo.FileUpload;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 约会人员
 * Created by hh on 2017.7.27 0027.
 */
@Entity
@Table(name = "user_info_base")
@Getter
@Setter
public class DatingUserVO {
    @Id
    private String id;
    private String username;
    private String phone;
    @Column(name = "nick_name")
    private String nickName;
    private Integer sex = -1;
    private Integer age = 0;
    private String intro;
    private String interest;
    private String job;
    private String constellation;
    private String birth;       //生日
    @Column(name = "user_photo_head")
    private String userPhotoHead;

    @Transient
    private Double distance;

    @Transient
    private List<FileUpload> albumList = new ArrayList<>();
}
