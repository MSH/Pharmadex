/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.enums.ReviewStatus;

/**
 *  Author: dudchenko
 */
@ManagedBean
@ViewScoped
public class ReviewDetailBnMZ implements Serializable {

	private static final long serialVersionUID = -607484272762279214L;

	@ManagedProperty(value = "#{reviewDetailBn}")
    private ReviewDetailBn reviewDetailBn;
	
	@ManagedProperty(value = "#{userSession}")
    private UserSession userSession;


	public boolean visibleSaveBtn(){
		//userSession.reviewer
		/*ReviewInfo info = reviewDetail.getReviewInfo();
		if(info != null && info.getReviewStatus().equals(ReviewStatus.ACCEPTED))
			return false;
		
		// curUser=Second Reviewer && status=SEC_REVIEW
		if(info.isSecreview() && info.getReviewStatus().equals(ReviewStatus.SEC_REVIEW)
				&& info.getReviewer().getUserId().intValue() == userSession.getLoggedINUserID().intValue())
			return false;*/
		
		return userSession.isReviewer();
	}
	
	public boolean hideComponents(){
		return !visibleSaveBtn();
	}
	
	public boolean visibleSubmitBtn(){
		//(userSession.reviewer) and (not reviewInfoBn.submitted)
		ReviewInfo info = getReviewDetailBn().getReviewDetail().getReviewInfo();
		if(info != null && info.getReviewStatus().equals(ReviewStatus.ACCEPTED))
			return false;
		
		if(info.isSecreview() && info.getReviewStatus().equals(ReviewStatus.SEC_REVIEW)
				&& info.getReviewer().getUserId().intValue() == userSession.getLoggedINUserID().intValue())
			return false;

		return true;
	}

	public ReviewDetailBn getReviewDetailBn() {
		return reviewDetailBn;
	}

	public void setReviewDetailBn(ReviewDetailBn reviewDetailBn) {
		this.reviewDetailBn = reviewDetailBn;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}
	
}
