package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by hh on 2017.11.3 0003.
 */
@Entity
@Table(name = "evaluate_detailed_item")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class EvaluateDetailedItem {

    public EvaluateDetailedItem(Long evaluateDetailedId, Long evaluateTypeId, Long itemId) {
        this.evaluateDetailedId = evaluateDetailedId;
        this.evaluateTypeId = evaluateTypeId;
        this.itemId = itemId;
    }

    public EvaluateDetailedItem() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long evaluateDetailedId;

    private Long evaluateTypeId;

    private Long itemId;

}
