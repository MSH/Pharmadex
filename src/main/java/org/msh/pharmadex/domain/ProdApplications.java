package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.ProdDrugType;
import org.msh.pharmadex.domain.enums.RegState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Entity
public class ProdApplications extends CreationDetail implements Serializable {
    private static final long serialVersionUID = 3054470055191648660L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "PROD_ID")
    private Product prod;

    @OneToOne
    @JoinColumn(name = "ORG_CNTRY_ID")
    private Country originCntry;

    @Column(length = 100)
    private String prodRelCntroler;

    @Column(nullable = false)
    private String relResponsibility;

    @Column(length = 100)
    private String Representative;

    private boolean packageInsert;

    private boolean labelEnclosed;

    @Column(length = 200)
    private String appComment;

    private ProdDrugType drugType;

    @Column(length = 20)
    private String packSize;

    @Column(length = 20)
    private String originCntryReg;

    @Column(length = 100)
    private String contType;

    @Column(length = 20)
    private String phyAppearance;

    @Column(length = 100)
    private String pharmacopeiaStds;

    @Column(length = 100)
    private String ssnCode;

    @Column(length = 100)
    private String inactiveIngr;

    @Column(length = 20)
    private String storageCndtn;

    @Column(length = 100)
    private String receiptNo;

    @Column(length = 100)
    private String waybillNo;

    private RegState regState;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Temporal(TemporalType.DATE)
    private Date lastStatusDate;

    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Temporal(TemporalType.DATE)
    private Date regExpiryDate;

    private boolean feeReceived;

    private boolean dossierReceived;

    private boolean applicantVerified;

    private boolean productVerified;

    @Column(length = 100)
    private String ackLetterNo;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<Mail> mails;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<Comment> comments;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<TimeLine> timeLines;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<ProdAppChecklist> prodAppChecklists;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<StatusUser> statusUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODERATOR_ID", nullable = true)
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @OneToOne
    private Appointment appointment;

    public Date getRegExpiryDate() {
        return regExpiryDate;
    }

    public void setRegExpiryDate(Date regExpiryDate) {
        this.regExpiryDate = regExpiryDate;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProd() {
        return prod;
    }

    public void setProd(Product prod) {
        this.prod = prod;
    }

    public Country getOriginCntry() {
        return originCntry;
    }

    public void setOriginCntry(Country originCntry) {
        this.originCntry = originCntry;
    }

    public String getProdRelCntroler() {
        return prodRelCntroler;
    }

    public void setProdRelCntroler(String prodRelCntroler) {
        this.prodRelCntroler = prodRelCntroler;
    }

    public String getRelResponsibility() {
        return relResponsibility;
    }

    public void setRelResponsibility(String relResponsibility) {
        this.relResponsibility = relResponsibility;
    }

    public String getRepresentative() {
        return Representative;
    }

    public void setRepresentative(String representative) {
        Representative = representative;
    }

    public boolean isPackageInsert() {
        return packageInsert;
    }

    public void setPackageInsert(boolean packageInsert) {
        this.packageInsert = packageInsert;
    }

    public boolean isLabelEnclosed() {
        return labelEnclosed;
    }

    public void setLabelEnclosed(boolean labelEnclosed) {
        this.labelEnclosed = labelEnclosed;
    }

    public String getAppComment() {
        return appComment;
    }

    public void setAppComment(String appComment) {
        this.appComment = appComment;
    }

    public ProdDrugType getDrugType() {
        return drugType;
    }

    public void setDrugType(ProdDrugType drugType) {
        this.drugType = drugType;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public String getOriginCntryReg() {
        return originCntryReg;
    }

    public void setOriginCntryReg(String originCntryReg) {
        this.originCntryReg = originCntryReg;
    }

    public String getContType() {
        return contType;
    }

    public void setContType(String contType) {
        this.contType = contType;
    }

    public String getPhyAppearance() {
        return phyAppearance;
    }

    public void setPhyAppearance(String phyAppearance) {
        this.phyAppearance = phyAppearance;
    }

    public String getPharmacopeiaStds() {
        return pharmacopeiaStds;
    }

    public void setPharmacopeiaStds(String pharmacopeiaStds) {
        this.pharmacopeiaStds = pharmacopeiaStds;
    }

    public String getSsnCode() {
        return ssnCode;
    }

    public void setSsnCode(String ssnCode) {
        this.ssnCode = ssnCode;
    }

    public String getInactiveIngr() {
        return inactiveIngr;
    }

    public void setInactiveIngr(String inactiveIngr) {
        this.inactiveIngr = inactiveIngr;
    }

    public String getStorageCndtn() {
        return storageCndtn;
    }

    public void setStorageCndtn(String storageCndtn) {
        this.storageCndtn = storageCndtn;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public RegState getRegState() {
        return regState;
    }

    public void setRegState(RegState regState) {
        this.regState = regState;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
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

    public String getAckLetterNo() {
        return ackLetterNo;
    }

    public void setAckLetterNo(String ackLetterNo) {
        this.ackLetterNo = ackLetterNo;
    }

    public boolean isFeeReceived() {
        return feeReceived;
    }

    public void setFeeReceived(boolean feeReceived) {
        this.feeReceived = feeReceived;
    }

    public boolean isDossierReceived() {
        return dossierReceived;
    }

    public void setDossierReceived(boolean dossierReceived) {
        this.dossierReceived = dossierReceived;
    }

    public List<Mail> getMails() {
        return mails;
    }

    public void setMails(List<Mail> mails) {
        this.mails = mails;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<TimeLine> getTimeLines() {
        return timeLines;
    }

    public void setTimeLines(List<TimeLine> timeLines) {
        this.timeLines = timeLines;
    }

    public List<ProdAppChecklist> getProdAppChecklists() {
        return prodAppChecklists;
    }

    public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        this.prodAppChecklists = prodAppChecklists;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }
}
