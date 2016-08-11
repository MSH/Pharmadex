package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
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
    private String revName;
    private ReviewStatus reviewStatus;
    private Date assignDate;
    private Date dueDate;
    private String ctdModule;
    private RecomendType recomendType;
    private Date submittedDate;
    private boolean pastDue;
    private ProdAppType prodAppType;
    private String prodAppNo;
    private boolean sra;
    private boolean fastrack;
    private Long prodAppID;
    private RegState regState;
    private Long secReviewerId;
    private Boolean secondary;


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

    public String getCtdModule() {
        return ctdModule;
    }

    public void setCtdModule(String ctdModule) {
        this.ctdModule = ctdModule;
    }

    public RecomendType getRecomendType() {
        return recomendType;
    }

    public void setRecomendType(RecomendType recomendType) {
        this.recomendType = recomendType;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public boolean isPastDue() {
        pastDue = false;
        if(reviewStatus.equals(ReviewStatus.SUBMITTED)||reviewStatus.equals(ReviewStatus.ACCEPTED)||reviewStatus.equals(ReviewStatus.FIR_APP_RESPONSE)) {
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

    public String getRevName() {
        return revName;
    }

    public void setRevName(String revName) {
        this.revName = revName;
    }

    public ProdAppType getProdAppType() {
        return prodAppType;
    }

    public void setProdAppType(ProdAppType prodAppType) {
        this.prodAppType = prodAppType;
    }

    public String getProdAppNo() {
        return prodAppNo;
    }

    public void setProdAppNo(String prodAppNo) {
        this.prodAppNo = prodAppNo;
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

    public Long getProdAppID() {
        return prodAppID;
    }

    public void setProdAppID(Long prodAppID) {
        this.prodAppID = prodAppID;
    }

	public RegState getRegState() {
		return regState;
	}

	public void setRegState(RegState regState) {
		this.regState = regState;
	}

	public Long getSecReviewerId() {
		return secReviewerId;
	}

	public void setSecReviewerId(Long secReviewerId) {
		this.secReviewerId = secReviewerId;
	}

	public Boolean isSecondary() {
		return secondary;
	}

	public void setSecondary(Boolean secondary) {
		this.secondary = secondary;
	}
    
    
}
