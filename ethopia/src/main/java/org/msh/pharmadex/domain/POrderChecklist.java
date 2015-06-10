package org.msh.pharmadex.domain;

import org.hibernate.type.YesNoType;
import org.msh.pharmadex.domain.enums.YesNoNA;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "porder_checklist")
public class POrderChecklist extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pipOrderLookup_id")
    private PIPOrderLookUp pipOrderLookUp;

    @ManyToOne
    @JoinColumn(name = "piporder_id", nullable = true)
    private PIPOrder pipOrder;

    @ManyToOne
    @JoinColumn(name = "purorder_id", nullable = true)
    private PurOrder purOrder;

    @Enumerated(value = EnumType.STRING)
    private YesNoNA value;

    @Enumerated(value = EnumType.STRING)
    private YesNoNA staffValue;

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

    public YesNoNA getValue() {
        return value;
    }

    public void setValue(YesNoNA value) {
        this.value = value;
    }

    public YesNoNA getStaffValue() {
        return staffValue;
    }

    public void setStaffValue(YesNoNA staffValue) {
        this.staffValue = staffValue;
    }

    public String getStaffComment() {
        return staffComment;
    }

    public void setStaffComment(String staffComment) {
        this.staffComment = staffComment;
    }

    public PIPOrder getPipOrder() {
        return pipOrder;
    }

    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
    }
}
