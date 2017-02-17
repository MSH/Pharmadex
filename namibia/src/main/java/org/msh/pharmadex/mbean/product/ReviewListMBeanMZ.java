package org.msh.pharmadex.mbean.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.service.ReviewServiceMZ;
import org.msh.pharmadex.service.UserAccessService;

@ManagedBean
@ViewScoped
public class ReviewListMBeanMZ implements Serializable {

	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;

	@ManagedProperty(value = "#{reviewService}")
	private ReviewService reviewService;


	@ManagedProperty(value = "#{userAccessService}")
	private UserAccessService userAccessService;

	private List<ReviewInfoTable> reviewInfoTables;
	private List<ReviewInfoTable> filteredReviewInfos;

	private List<ReviewInfoTable> allReviews;
	private Workspace workspace;

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public List<ReviewInfoTable> getReviewInfoTables() {
		reviewInfoTables = reviewService.findRevInfoTableByReviewer(userSession.getLoggedINUserID());

		return reviewInfoTables;
	}

	public void setReviewInfoTables(List<ReviewInfoTable> reviewInfoTables) {
		this.reviewInfoTables = reviewInfoTables;
	}	

	public List<ReviewInfoTable> getAllReviews() {
		if (allReviews == null) {
			allReviews = reviewService.findAllPriSecReview();
		}
		return allReviews;
	}

	public void setAllReviews(List<ReviewInfoTable> allReviews) {
		this.allReviews = allReviews;
	}

	public List<ReviewInfoTable> getFilteredReviewInfos() {
		return filteredReviewInfos;
	}

	public void setFilteredReviewInfos(List<ReviewInfoTable> filteredReviewInfos) {
		this.filteredReviewInfos = filteredReviewInfos;
	}

	public ReviewService getReviewService() {
		return reviewService;
	}

	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	public Workspace getWorkspace() {
		if(null == workspace)
			workspace = userAccessService.getWorkspace();
		return workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	public UserAccessService getUserAccessService() {
		return userAccessService;
	}

	public void setUserAccessService(UserAccessService userAccessService) {
		this.userAccessService = userAccessService;
	}
}
