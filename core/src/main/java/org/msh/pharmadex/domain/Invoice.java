package org.msh.pharmadex.domain;


import org.msh.pharmadex.domain.enums.InvoiceType;
import org.msh.pharmadex.domain.enums.PaymentStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_date", nullable = false)
    private Date issueDate;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "invoice_amt", length = 100, nullable = false)
    private String invoiceAmt;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_amt", length = 100, nullable = false)
    private String paymentAmt;

    @Temporal(TemporalType.DATE)
    @Column(name = "payment_date")
    private Date paymentDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "curr_expiry_date", nullable = false)
    private Date currExpDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "new_expiry_date", nullable = false)
    private Date newExpDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "renewal_date")
    private Date renewalDate;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.ALL})
    private List<Reminder> reminders;

    @Lob
    @Column(nullable = true)
    private byte[] invoiceFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceAmt() {
        return invoiceAmt;
    }

    public void setInvoiceAmt(String invoiceAmt) {
        this.invoiceAmt = invoiceAmt;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getCurrExpDate() {
        return currExpDate;
    }

    public void setCurrExpDate(Date currExpDate) {
        this.currExpDate = currExpDate;
    }

    public Date getNewExpDate() {
        return newExpDate;
    }

    public void setNewExpDate(Date newExpDate) {
        this.newExpDate = newExpDate;
    }

    public Date getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(Date renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(String paymentAmt) {
        this.paymentAmt = paymentAmt;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public byte[] getInvoiceFile() {
        return invoiceFile;
    }

    public void setInvoiceFile(byte[] invoiceFile) {
        this.invoiceFile = invoiceFile;
    }
}
