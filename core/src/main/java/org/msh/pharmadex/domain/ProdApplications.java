package org.msh.pharmadex.domain;

import org.hibernate.envers.Audited;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.ProdDrugType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.UseCategory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Entity
@Audited
public class ProdApplications extends CreationDetail implements Serializable {
    private static final long serialVersionUID = 3054470055191648660L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "PROD_ID")
    private Product prod;

    @OneToOne(cascade = CascadeType.ALL)
    private Pricing pricing;

    @Column(length = 255)
    private String prodAppNo;

    @Column(length = 500)
    private String ingrdStatment;

    private boolean packageInsert;

    private boolean labelEnclosed;

    @Column(length = 500)
    private String appComment;

    @Enumerated(EnumType.STRING)
    private ProdDrugType drugType;

    @Enumerated(EnumType.STRING)
    private ProdAppType prodAppType;

    @ElementCollection(targetClass = UseCategory.class)
    @JoinTable(name = "tblusecategories", joinColumns = @JoinColumn(name = "prodAppID"))
    @Column(name = "useCategory")
    @Enumerated(EnumType.STRING)
    private List<UseCategory> useCategories;

    @Column(length = 500)
    private String packSize;

    @Column(length = 500)
    private String contType;

    @Column(length = 500)
    private String shelfLife;

    @Column(length = 500)
    private String pharmacopeiaStds;

    @Column(length = 500)
    private String ssnCode;

    @Column(length = 500)
    private String inactiveIngr;

    @Column(length = 500)
    private String storageCndtn;

    @Column(length = 500)
    private String receiptNo;

    @Column(length = 500)
    private String bankName;

    @Column(length = 1500)
    private String indications;

    @Column(length = 500)
    private String posology;

    @Column(length = 255)
    private String feeAmt;

    @Temporal(TemporalType.DATE)
    private Date feeSubmittedDt;

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

    private boolean feeReceived;

    private boolean dossierReceived;

    private boolean applicantVerified;

    private boolean productVerified;

    private boolean sra;

    private boolean fastrack;

    private boolean sampleRecieved;

    private boolean sampleRequestSend;

    @Column(length = 500)
    private String ackLetterNo;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<ForeignAppStatus> foreignAppStatus;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<Mail> mails;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<Comment> comments;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<TimeLine> timeLines;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<ProdAppChecklist> prodAppChecklists;

    @OneToOne(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private StatusUser statusUser;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<Review> reviews;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "prodApplications", cascade = {CascadeType.ALL})
    private List<ProdAppAmdmt> prodAppAmdmts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODERATOR_ID", nullable = true)
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @OneToOne(cascade = CascadeType.ALL)
    private Appointment appointment;

    @Column(length = 500)
    private String dossLoc;

    private boolean sendToGazette;

    @Lob
    @Column(nullable = true)
    private byte[] regCert;

    @Column(length = 255)
    private String username;

    @Column(length = 255)
    private String position;

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

    public String getContType() {
        return contType;
    }

    public void setContType(String contType) {
        this.contType = contType;
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

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<ProdAppAmdmt> getProdAppAmdmts() {
        return prodAppAmdmts;
    }

    public void setProdAppAmdmts(List<ProdAppAmdmt> prodAppAmdmts) {
        this.prodAppAmdmts = prodAppAmdmts;
    }

    public byte[] getRegCert() {
        return regCert;
    }

    public void setRegCert(byte[] regCert) {
        this.regCert = regCert;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getProdAppNo() {
        return prodAppNo;
    }

    public void setProdAppNo(String prodAppNo) {
        this.prodAppNo = prodAppNo;
    }

    public String getIngrdStatment() {
        return ingrdStatment;
    }

    public void setIngrdStatment(String ingrdStatment) {
        this.ingrdStatment = ingrdStatment;
    }

    public String getDossLoc() {
        return dossLoc;
    }

    public void setDossLoc(String dossLoc) {
        this.dossLoc = dossLoc;
    }

    public ProdAppType getProdAppType() {
        return prodAppType;
    }

    public void setProdAppType(ProdAppType prodAppType) {
        this.prodAppType = prodAppType;
    }

    public boolean isSendToGazette() {
        return sendToGazette;
    }

    public void setSendToGazette(boolean sendToGazette) {
        this.sendToGazette = sendToGazette;
    }

    public String getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getFeeSubmittedDt() {
        return feeSubmittedDt;
    }

    public void setFeeSubmittedDt(Date feeSubmittedDt) {
        this.feeSubmittedDt = feeSubmittedDt;
    }

    public List<ForeignAppStatus> getForeignAppStatus() {
        return foreignAppStatus;
    }

    public void setForeignAppStatus(List<ForeignAppStatus> foreignAppStatus) {
        this.foreignAppStatus = foreignAppStatus;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public String getPosology() {
        return posology;
    }

    public void setPosology(String posology) {
        this.posology = posology;
    }

    public List<UseCategory> getUseCategories() {
        return useCategories;
    }

    public void setUseCategories(List<UseCategory> useCategories) {
        this.useCategories = useCategories;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
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

    public String getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(String feeAmt) {
        this.feeAmt = feeAmt;
    }

    public boolean isSampleRecieved() {
        return sampleRecieved;
    }

    public void setSampleRecieved(boolean sampleRecieved) {
        this.sampleRecieved = sampleRecieved;
    }

    public boolean isSampleRequestSend() {
        return sampleRequestSend;
    }

    public void setSampleRequestSend(boolean sampleRequestSend) {
        this.sampleRequestSend = sampleRequestSend;
    }
}

