package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "payment")
public class Payment extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "inv_id")
    private Invoice invoice;


    @Column(name = "payment_amt", length = 100, nullable = true)
    private String paymentAmt;

    @Temporal(TemporalType.DATE)
    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "receipt_no", length = 255)
    private String receiptNo;

    @Column(length = 255, name = "tracking_no")
    private String trackingNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(String paymentAmt) {
        this.paymentAmt = paymentAmt;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }
}
