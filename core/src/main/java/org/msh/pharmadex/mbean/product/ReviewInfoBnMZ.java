/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.DisplayReviewInfo;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;
import org.msh.pharmadex.service.ReviewServiceMZ;
import org.msh.pharmadex.util.RetObject;

/**
 * Backing bean to capture review of products
 * Author: dudchenko
 */
@ManagedBean
@ViewScoped
public class ReviewInfoBnMZ implements Serializable {

	@ManagedProperty(value = "#{reviewInfoBn}")
	private ReviewInfoBn reviewInfoBn;

	@ManagedProperty(value = "#{reviewServiceMZ}")
	private ReviewServiceMZ reviewServiceMZ;

	@ManagedProperty(value = "#{prodApplicationsServiceMZ}")
	ProdApplicationsServiceMZ prodApplicationsServiceMZ;

	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");


	public String submitComment() {
		facesContext = FacesContext.getCurrentInstance();
		bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		try {
			ReviewInfo reviewInfo = getReviewInfoBn().getReviewInfo();
			ReviewComment reviewComment = getReviewInfoBn().getReviewComment();

			RetObject retObject = reviewServiceMZ.submitReviewInfo(reviewInfo, reviewComment, userSession.getLoggedINUserID());
			if (retObject.getMsg().equals("success")) {
				reviewInfo = (ReviewInfo) retObject.getObj();
				getReviewInfoBn().setReviewInfo(reviewInfo);
				getReviewInfoBn().setReviewComment(reviewComment);
				getReviewInfoBn().getReviewComments().add(reviewComment);
				facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
			} else if (retObject.getMsg().equals("close_def")) {
				facesContext.addMessage(null, new FacesMessage(bundle.getString("resolve_def")));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
		}
		return "/public/registrationhome";
	}

	public String buildStyleClassName(DisplayReviewInfo q){
		String white = "review_question_active", grey = "review_question_inactive";
		// quest.save?'review_question_inactive':'review_question_active'

		ReviewDetail item = reviewServiceMZ.getReviewService().findReviewDetails(q);
		if(item != null){
			Long create = (item.getCreatedBy() != null ? item.getCreatedBy().getUserId() : 0);
			Long update = (item.getUpdatedBy() != null ? item.getUpdatedBy().getUserId() : 0);

			if(create > 0 && update > 0){
				if(userSession.getLoggedINUserID().intValue() == update.intValue())
					return grey;
			}
		}
		return white;
	}
	
	public String generateLetter() {
        facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {
            getReviewInfoBn().getRevDeficiency().setSentComment(getReviewInfoBn().getReviewComment());
            getReviewInfoBn().getRevDeficiency().setUser(getReviewInfoBn().getReviewComment().getUser());
            getReviewInfoBn().setSubmitDate(new Date());
            getReviewInfoBn().getReviewComments().add(getReviewInfoBn().getReviewComment());
            getReviewInfoBn().setReviewStatus(ReviewStatus.FIR_SUBMIT);
            getReviewInfoBn().getRevDeficiency().setReviewInfo(getReviewInfoBn().getReviewInfo());
            getReviewInfoBn().getRevDeficiency().setCreatedDate(new Date());
            String com = "";
            if(getReviewInfoBn().getReviewComment()!=null)
            	if(getReviewInfoBn().getReviewComment().getComment()!=null)
            		com = getReviewInfoBn().getReviewComment().getComment();
            
            RetObject rez = prodApplicationsServiceMZ.
            		createReviewDeficiencyLetter(getReviewInfoBn().getProdApplications(),com,
            				getReviewInfoBn().getRevDeficiency());
            
            if (rez.getMsg().equals("success")) {
            	getReviewInfoBn().setReviewInfo((ReviewInfo) rez.getObj());            	
                facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
                getReviewInfoBn().setReviewComments(getReviewInfoBn().getReviewComments()); 
                getReviewInfoBn().setRevDeficiencies(null);
            }else{
            	javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
            }
        
        } catch (Exception ex) {
            ex.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), ""));
        }
        return "";
    }

	public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
		return prodApplicationsServiceMZ;
	}

	public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsServiceMZ) {
		this.prodApplicationsServiceMZ = prodApplicationsServiceMZ;
	}

	public ReviewInfoBn getReviewInfoBn() {
		return reviewInfoBn;
	}

	public void setReviewInfoBn(ReviewInfoBn reviewInfoBn) {
		this.reviewInfoBn = reviewInfoBn;
	}

	public ReviewServiceMZ getReviewServiceMZ() {
		return reviewServiceMZ;
	}

	public void setReviewServiceMZ(ReviewServiceMZ reviewService) {
		this.reviewServiceMZ = reviewService;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public boolean visibleUpdateBtn(){
		//!reviewInfoBn.readOnly
		/*ReviewInfo info = reviewInfoBn.getReviewInfo();
		if(info != null && info.getReviewStatus().equals(ReviewStatus.ACCEPTED))
			return false;*/

		// curUser=Second Reviewer && status=SEC_REVIEW
		/*if(info.isSecreview() && info.getReviewStatus().equals(ReviewStatus.SEC_REVIEW)
				&& info.getReviewer().getUserId().intValue() == userSession.getLoggedINUserID().intValue())
			return false;*/

		return !getReviewInfoBn().isReadOnly();
	}

	public boolean visibleViewRespBtn(){
		return getReviewInfoBn().isReadOnly();
	}

	public void printReview(){
		ProdApplications prodApplications = getReviewInfoBn().getProdApplications();
		if(prodApplications != null){
			String s = getProdApplicationsServiceMZ().createReviewDetails(prodApplications);
			if(!s.equals("persist")){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
			}else{
				javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
		}
	}

}
