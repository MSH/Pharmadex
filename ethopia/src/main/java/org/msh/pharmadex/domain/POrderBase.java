package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.RecomendType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by utkarsh on 5/12/15.
 */
@MappedSuperclass
public class POrderBase extends CreationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255)
    private String shippingInstruction;

    @Column(length = 255)
    private String appNo;

    @Column(length = 255)
    private String delivery;

    @Column(length = 255)
    private String paymentMethod;

    @Column(length = 255)
    private String entryPort;

    @Column(length = 500)
    private String comment;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Temporal(TemporalType.DATE)
    private Date approvalDate;

    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    private boolean feeRecieved;

    @Temporal(TemporalType.DATE)
    private Date feeRecieveDate;

    @OneToOne
    private Applicant applicant;

    @OneToOne
    private User applicantUser;

    @OneToOne
    private User processor;

    @OneToOne
    private User createdBy;

    @OneToOne
    private User updatedBy;

    @Enumerated(EnumType.STRING)
    private AmdmtState state;

    private boolean appVerified;

    @Enumerated(EnumType.STRING)
    private RecomendType reviewState;

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

    public String getEntryPort() {
        return entryPort;
    }

    public void setEntryPort(String entryPort) {
        this.entryPort = entryPort;
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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
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

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    public boolean isFeeRecieved() {
        return feeRecieved;
    }

    public void setFeeRecieved(boolean feeRecieved) {
        this.feeRecieved = feeRecieved;
    }

    public Date getFeeRecieveDate() {
        return feeRecieveDate;
    }

    public void setFeeRecieveDate(Date feeRecieveDate) {
        this.feeRecieveDate = feeRecieveDate;
    }

    public User getProcessor() {
        return processor;
    }

    public void setProcessor(User processor) {
        this.processor = processor;
    }

    public boolean isAppVerified() {
        return appVerified;
    }

    public void setAppVerified(boolean appVerified) {
        this.appVerified = appVerified;
    }

    public RecomendType getReviewState() {
        return reviewState;
    }

    public void setReviewState(RecomendType reviewState) {
        this.reviewState = reviewState;
    }
}
