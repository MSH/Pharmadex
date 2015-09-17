/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.SuspensionStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "susp_detail")
public class SuspDetail extends CreationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "susp_no", nullable = false)
    private String suspNo;

    @Column(name = "final_summ", nullable = true)
    @Lob
    private String finalSumm;

    @Enumerated(EnumType.STRING)
    private SuspensionStatus suspensionStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_date")
    private Date reqDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "decision_date")
    private Date decisionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "due_date")
    private Date dueDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "susp_st_date")
    private Date suspStDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "susp_end_date")
    private Date suspEndDate;

    @Enumerated(EnumType.STRING)
    private RegState decision;

    @OneToOne
    private User moderator;

    @OneToOne
    private User reviewer;

    @Column(name = "canceled")
    private boolean canceled;

    @Column(name = "complete")
    private boolean complete;

    @OneToMany(mappedBy = "suspDetail", cascade = {CascadeType.ALL})
    private List<SuspComment> suspComments;

    @OneToMany(mappedBy = "suspDetail", cascade = {CascadeType.ALL})
    private List<ProdAppLetter> prodAppLetters;

    @ManyToOne
    private ProdApplications prodApplications;

    public SuspDetail() {
    }

    public SuspDetail(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public SuspDetail(ProdApplications prodApplications, List<SuspComment> suspComments) {
        this.prodApplications = prodApplications;
        this.suspComments = suspComments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public List<SuspComment> getSuspComments() {
        return suspComments;
    }

    public void setSuspComments(List<SuspComment> suspComments) {
        this.suspComments = suspComments;
    }

    public List<ProdAppLetter> getProdAppLetters() {
        return prodAppLetters;
    }

    public void setProdAppLetters(List<ProdAppLetter> prodAppLetters) {
        this.prodAppLetters = prodAppLetters;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public String getSuspNo() {
        return suspNo;
    }

    public void setSuspNo(String suspNo) {
        this.suspNo = suspNo;
    }

    public SuspensionStatus getSuspensionStatus() {
        return suspensionStatus;
    }

    public void setSuspensionStatus(SuspensionStatus suspensionStatus) {
        this.suspensionStatus = suspensionStatus;
    }

    public String getFinalSumm() {
        return finalSumm;
    }

    public void setFinalSumm(String finalSumm) {
        this.finalSumm = finalSumm;
    }

    public RegState getDecision() {
        return decision;
    }

    public void setDecision(RegState decision) {
        this.decision = decision;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getSuspStDate() {
        return suspStDate;
    }

    public void setSuspStDate(Date suspStDate) {
        this.suspStDate = suspStDate;
    }

    public Date getSuspEndDate() {
        return suspEndDate;
    }

    public void setSuspEndDate(Date suspEndDate) {
        this.suspEndDate = suspEndDate;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}