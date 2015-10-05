package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.ReviewStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by utkarsh on 4/6/15.
 */
public class ReviewInfoTable implements Serializable {

    private Long id;
    private String revType;
    private String prodName;
    private ReviewStatus reviewStatus;
    private Date assignDate;
    private Date dueDate;
    private String ctdModule;
    private RecomendType recomendType;
    private Date submittedDate;
    private boolean pastDue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRevType() {
        return revType;
    }

    public void setRevType(String revType) {
        this.revType = revType;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Date getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setCtdModule(String ctdModule) {
        this.ctdModule = ctdModule;
    }

    public String getCtdModule() {
        return ctdModule;
    }

    public void setRecomendType(RecomendType recomendType) {
        this.recomendType = recomendType;
    }

    public RecomendType getRecomendType() {
        return recomendType;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public boolean isPastDue() {
        pastDue = false;
        if(reviewStatus.equals(ReviewStatus.SUBMITTED)||reviewStatus.equals(ReviewStatus.ACCEPTED)||reviewStatus.equals(ReviewStatus.APPLICANT_RFI)) {
            pastDue = false;
        }else{
            Date currDate = new Date();
            if(dueDate!=null&&currDate.after(dueDate)){
                pastDue = true;
            }
        }
        return pastDue;
    }

    public void setPastDue(boolean pastDue) {
        this.pastDue = pastDue;
    }
}
