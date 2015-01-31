package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "purorder_checklist")
public class PurOrderChecklist extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pipOrderLookup_id")
    private PIPOrderLookUp pipOrderLookUp;

    @ManyToOne
    @JoinColumn(name = "purOrder_id")
    private PurOrder purOrder;

    private boolean value;

    private boolean staffValue;

    @Column(length = 500)
    private String staffComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PIPOrderLookUp getPipOrderLookUp() {
        return pipOrderLookUp;
    }

    public void setPipOrderLookUp(PIPOrderLookUp pipOrderLookUp) {
        this.pipOrderLookUp = pipOrderLookUp;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isStaffValue() {
        return staffValue;
    }

    public void setStaffValue(boolean staffValue) {
        this.staffValue = staffValue;
    }

    public String getStaffComment() {
        return staffComment;
    }

    public void setStaffComment(String staffComment) {
        this.staffComment = staffComment;
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
    }
}
