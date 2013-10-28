package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.ReminderType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "reminder")
public class Reminder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "sent_date")
    private Date sentDate;

    @Enumerated(EnumType.STRING)
    private ReminderType reminderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public ReminderType getReminderType() {
        return reminderType;
    }

    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
