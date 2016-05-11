/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
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

    @Column(name = "moderator_summ", nullable = true)
    @Lob
    private String moderatorSumm;

    @Column(name = "head_summ", nullable = true)
    @Lob
    private String headSumm;

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
    private RecomendType decision;

    @OneToOne
    private User moderator;

    @OneToOne
    private User reviewer;

    @Column(name = "canceled")
    private boolean canceled;

    @Column(name = "complete")
    private boolean complete;

    @OneToMany(mappedBy = "suspDetail", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<SuspComment> suspComments;

    @OneToMany(mappedBy = "suspDetail", cascade = {CascadeType.ALL})
    private List<ProdAppLetter> prodAppLetters;

    @ManyToOne
    private ProdApplications prodApplications;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "batchNo", length = 500)
    private String batchNo;

    @Column(name = "orgReported", length = 500)
    private String orgReported;

    @Temporal(TemporalType.TIMESTAMP)
    private Date notifRecieveDt;

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
        finalSumm = finalSumm.replaceAll("/","");
        return finalSumm.replaceAll("<\\w*>","");
    }

    public void setFinalSumm(String finalSumm) {
        this.finalSumm = finalSumm;
    }

    public String getModeratorSumm() {
        finalSumm = finalSumm.replaceAll("\\\\","");
        return moderatorSumm.replaceAll("<\\w+>","");
    }

    public void setModeratorSumm(String moderatorSumm) {
        this.moderatorSumm = moderatorSumm;
    }

    public RecomendType getDecision() {
        return decision;
    }

    public void setDecision(RecomendType decision) {
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getOrgReported() {
        return orgReported;
    }

    public void setOrgReported(String orgReported) {
        this.orgReported = orgReported;
    }

    public Date getNotifRecieveDt() {
        return notifRecieveDt;
    }

    public void setNotifRecieveDt(Date notifRecieveDt) {
        this.notifRecieveDt = notifRecieveDt;
    }
}
