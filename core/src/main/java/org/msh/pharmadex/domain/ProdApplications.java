package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Entity
//@Audited
public class ProdApplications extends CreationDetail implements Serializable {
    private static final long serialVersionUID = 3054470055191648660L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_ID", nullable = false)
    private Applicant applicant;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROD_ID", nullable = true)
    private Product product;

    @Column(length = 255)
    private String prodAppNo;

    @Column(length = 255)
    private String prodRegNo;

    @Column(length = 500)
    private String appComment;

    @Enumerated(EnumType.STRING)
    private ProdAppType prodAppType;

    @Column(length = 500)
    private String receiptNo;

    @Column(length = 500)
    private String prescreenReceiptNo;

    @Column(length = 500)
    private String bankName;

    @Column(length = 500)
    private String prescreenBankName;

    @Column(length = 255)
    private String feeAmt;

    @Column(length = 255)
    private String prescreenfeeAmt;

    @Temporal(TemporalType.DATE)
    private Date prescreenfeeSubmittedDt;

    @Temporal(TemporalType.DATE)
    private Date feeSubmittedDt;

    @Lob
    @Column(nullable = true)
    private byte[] feeReceipt;

    @OneToOne
    private Attachment cRevAttach;

    @Lob
    @Column(nullable = true)
    private byte[] clinicalReview;

    @Enumerated(EnumType.STRING)
    private RegState regState;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Temporal(TemporalType.DATE)
    private Date lastStatusDate;

    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Temporal(TemporalType.DATE)
    private Date regExpiryDate;

    private boolean active;

    private boolean feeReceived;

    private boolean clinicalRevReceived;

    private boolean prescreenfeeReceived;

    private boolean dossierReceived;

    @Temporal(TemporalType.DATE)
    private Date dosRecDate;

    private boolean applicantVerified;

    private boolean productVerified;

    private boolean clinicalRevVerified;

    private boolean sra;

    private boolean fastrack;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<ProdAppAmdmt> prodAppAmdmts;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<ReviewInfo> reviewInfos;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<Review> reviews;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MODERATOR_ID", nullable = true)
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicantUser")
    private User applicantUser;

    @OneToOne(cascade = CascadeType.ALL)
    private Appointment appointment;

    @Column(length = 500)
    private String dossLoc;

    @Lob
    @Column(name = "exec_summary")
    private String execSummary;

    private boolean sendToGazette;

    @Lob
    @Column(nullable = true)
    private byte[] regCert;

    @Lob
    @Column(nullable = true)
    private byte[] rejCert;

    @Column(length = 255)
    private String username;

    @Column(length = 255)
    private String position;

    @Transient
    private ReviewStatus reviewStatus;

    @Column(length = 255, name = "priorityNo")
    private String priorityNo;


    public ProdApplications(Product prod, Applicant applicant) {
        this.product = prod;
        this.applicant = applicant;
    }

    public ProdApplications(Product prod) {
        this.product = prod;
    }

    public ProdApplications() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public String getProdAppNo() {
        return prodAppNo;
    }

    public void setProdAppNo(String prodAppNo) {
        this.prodAppNo = prodAppNo;
    }

    public String getAppComment() {
        return appComment;
    }

    public void setAppComment(String appComment) {
        this.appComment = appComment;
    }

    public ProdAppType getProdAppType() {
        return prodAppType;
    }

    public void setProdAppType(ProdAppType prodAppType) {
        this.prodAppType = prodAppType;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getPrescreenReceiptNo() {
        return prescreenReceiptNo;
    }

    public void setPrescreenReceiptNo(String prescreenReceiptNo) {
        this.prescreenReceiptNo = prescreenReceiptNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPrescreenBankName() {
        return prescreenBankName;
    }

    public void setPrescreenBankName(String prescreenBankName) {
        this.prescreenBankName = prescreenBankName;
    }

    public String getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(String feeAmt) {
        this.feeAmt = feeAmt;
    }

    public String getPrescreenfeeAmt() {
        return prescreenfeeAmt;
    }

    public void setPrescreenfeeAmt(String prescreenfeeAmt) {
        this.prescreenfeeAmt = prescreenfeeAmt;
    }

    public Date getPrescreenfeeSubmittedDt() {
        return prescreenfeeSubmittedDt;
    }

    public void setPrescreenfeeSubmittedDt(Date prescreenfeeSubmittedDt) {
        this.prescreenfeeSubmittedDt = prescreenfeeSubmittedDt;
    }

    public Date getFeeSubmittedDt() {
        return feeSubmittedDt;
    }

    public void setFeeSubmittedDt(Date feeSubmittedDt) {
        this.feeSubmittedDt = feeSubmittedDt;
    }

    public RegState getRegState() {
        return regState;
    }

    public void setRegState(RegState regState) {
        this.regState = regState;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getLastStatusDate() {
        return lastStatusDate;
    }

    public void setLastStatusDate(Date lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getRegExpiryDate() {
        return regExpiryDate;
    }

    public void setRegExpiryDate(Date regExpiryDate) {
        this.regExpiryDate = regExpiryDate;
    }

    public boolean isFeeReceived() {
        return feeReceived;
    }

    public void setFeeReceived(boolean feeReceived) {
        this.feeReceived = feeReceived;
    }

    public boolean isPrescreenfeeReceived() {
        return prescreenfeeReceived;
    }

    public void setPrescreenfeeReceived(boolean prescreenfeeReceived) {
        this.prescreenfeeReceived = prescreenfeeReceived;
    }

    public boolean isDossierReceived() {
        return dossierReceived;
    }

    public void setDossierReceived(boolean dossierReceived) {
        this.dossierReceived = dossierReceived;
    }

    public boolean isApplicantVerified() {
        return applicantVerified;
    }

    public void setApplicantVerified(boolean applicantVerified) {
        this.applicantVerified = applicantVerified;
    }

    public boolean isProductVerified() {
        return productVerified;
    }

    public void setProductVerified(boolean productVerified) {
        this.productVerified = productVerified;
    }

    public boolean isSra() {
        return sra;
    }

    public void setSra(boolean sra) {
        this.sra = sra;
    }

    public boolean isFastrack() {
        return fastrack;
    }

    public void setFastrack(boolean fastrack) {
        this.fastrack = fastrack;
    }

    public List<ProdAppAmdmt> getProdAppAmdmts() {
        return prodAppAmdmts;
    }

    public void setProdAppAmdmts(List<ProdAppAmdmt> prodAppAmdmts) {
        this.prodAppAmdmts = prodAppAmdmts;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getDossLoc() {
        return dossLoc;
    }

    public void setDossLoc(String dossLoc) {
        this.dossLoc = dossLoc;
    }

    public boolean isSendToGazette() {
        return sendToGazette;
    }

    public void setSendToGazette(boolean sendToGazette) {
        this.sendToGazette = sendToGazette;
    }

    public byte[] getRegCert() {
        return regCert;
    }

    public void setRegCert(byte[] regCert) {
        this.regCert = regCert;
    }

    public byte[] getRejCert() {
        return rejCert;
    }

    public void setRejCert(byte[] rejCert) {
        this.rejCert = rejCert;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProdRegNo() {
        return prodRegNo;
    }

    public void setProdRegNo(String prodRegNo) {
        this.prodRegNo = prodRegNo;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public List<ReviewInfo> getReviewInfos() {
        return reviewInfos;
    }

    public void setReviewInfos(List<ReviewInfo> reviewInfos) {
        this.reviewInfos = reviewInfos;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getExecSummary() {
        return execSummary;
    }

    public void setExecSummary(String execSummary) {
        this.execSummary = execSummary;
    }

    public ReviewStatus getReviewStatus() {
        if(regState.equals(RegState.REVIEW_BOARD)){
            if(reviewInfos==null)
                reviewStatus = ReviewStatus.NOT_ASSIGNED;
            else if(reviewInfos.size()<1)
                reviewStatus = ReviewStatus.NOT_ASSIGNED;
            else {
                reviewStatus = ReviewStatus.NOT_ASSIGNED;
                for(ReviewInfo reviewInfo : reviewInfos){
                    if (reviewStatus.ordinal() < reviewInfo.getReviewStatus().ordinal())
                        reviewStatus = reviewInfo.getReviewStatus();
                }
            }

        }else{
            reviewStatus = null;
        }
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Date getDosRecDate() {
        return dosRecDate;
    }

    public void setDosRecDate(Date dosRecDate) {
        this.dosRecDate = dosRecDate;
    }

    public byte[] getFeeReceipt() {
        return feeReceipt;
    }

    public void setFeeReceipt(byte[] feeReceipt) {
        this.feeReceipt = feeReceipt;
    }

    public Attachment getcRevAttach() {
        return cRevAttach;
    }

    public void setcRevAttach(Attachment cRevAttach) {
        this.cRevAttach = cRevAttach;
    }

    public byte[] getClinicalReview() {
        return clinicalReview;
    }

    public void setClinicalReview(byte[] clinicalReview) {
        this.clinicalReview = clinicalReview;
    }

    public boolean isClinicalRevReceived() {
        return clinicalRevReceived;
    }

    public void setClinicalRevReceived(boolean clinicalRevReceived) {
        this.clinicalRevReceived = clinicalRevReceived;
    }

    public boolean isClinicalRevVerified() {
        return clinicalRevVerified;
    }

    public void setClinicalRevVerified(boolean clinicalRevVerified) {
        this.clinicalRevVerified = clinicalRevVerified;
    }

    @Override
    public String toString() {
        return "" + id;
    }

    public String getPriorityNo() {
        return priorityNo;
    }

    public void setPriorityNo(String priorityNo) {
        this.priorityNo = priorityNo;
    }
}

