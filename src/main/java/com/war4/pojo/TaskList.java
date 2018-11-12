package com.war4.pojo;

import com.war4.enums.TaskListType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 任务列表
 * Created by hh on 2017.10.21 0021.
 */
@Entity
@Table(name = "task_list")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter@NoArgsConstructor
public class TaskList {
    public static final Byte CREATE = 0;//创建
    public static final Byte FINISH = 1;//完成

    public TaskList(String userId, String objectId, TaskListType type, String remark) {
        this.userId = userId;
        this.objectId = objectId;
        this.type = type.getValue();
        this.remark = remark;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Timestamp created;

    private Date updated;

    private Byte state;

    private String objectId;

    private Date objectTime;

    private String type;

    private String remark;

    @Version
    private Integer version;

    @Transient
    private Object obj;
}
