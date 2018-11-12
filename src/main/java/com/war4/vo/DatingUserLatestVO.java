package com.war4.vo;

import com.war4.pojo.FileUpload;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * 约会人员
 * Created by hh on 2017.7.27 0027.
 */
@Entity
@Getter@Setter
public class DatingUserLatestVO {

    @Id
    private String id;
    private String nickName;
    private Integer sex = -1;
    private Integer age = 0;
    private String constellation;
    private String userPhotoHead;
    private Double distance;
//    private Integer internalFlag;
    @Transient
    private Integer albumSize = 0;

    @Transient
    private List<FileUpload> albumList = new ArrayList<>();
}
