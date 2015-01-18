package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.AmdmtState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pip_order")
public class PIPOrder extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "pipOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PIPProd> pipProds;

    @Column(length = 255)
    private String shippingInstruction;

    @Column(length = 255)
    private String delivery;

    @Column(length = 255)
    private String paymentMethod;

    @Column(length = 255)
    private String fileNumber;

    @Column(length = 500)
    private String comment;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Temporal(TemporalType.DATE)
    private Date approvalDate;

    @OneToMany(mappedBy = "pipOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PIPOrderChecklist> pipOrderChecklists;

    @OneToOne
    private User createdBy;

    @OneToOne
    private User updatedBy;

    private AmdmtState state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PIPProd> getPipProds() {
        return pipProds;
    }

    public void setPipProds(List<PIPProd> pipProds) {
        this.pipProds = pipProds;
    }

    public String getShippingInstruction() {
        return shippingInstruction;
    }

    public void setShippingInstruction(String shippingInstruction) {
        this.shippingInstruction = shippingInstruction;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public List<PIPOrderChecklist> getPipOrderChecklists() {
        return pipOrderChecklists;
    }

    public void setPipOrderChecklists(List<PIPOrderChecklist> pipOrderChecklists) {
        this.pipOrderChecklists = pipOrderChecklists;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public AmdmtState getState() {
        return state;
    }

    public void setState(AmdmtState state) {
        this.state = state;
    }
}
