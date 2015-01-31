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
@Table(name = "pur_order")
public class PurOrder extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "purOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PurProd> purProds;

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

    @OneToMany(mappedBy = "purOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PurOrderChecklist> purOrderChecklists;

    @OneToOne
    private Applicant applicant;

    @OneToOne
    private User applicantUser;

    @OneToOne
    private User createdBy;

    @OneToOne
    private User updatedBy;

    @Enumerated(EnumType.STRING)
    private AmdmtState state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public List<PurProd> getPurProds() {
        return purProds;
    }

    public void setPurProds(List<PurProd> purProds) {
        this.purProds = purProds;
    }

    public List<PurOrderChecklist> getPurOrderChecklists() {
        return purOrderChecklists;
    }

    public void setPurOrderChecklists(List<PurOrderChecklist> purOrderChecklists) {
        this.purOrderChecklists = purOrderChecklists;
    }
}
