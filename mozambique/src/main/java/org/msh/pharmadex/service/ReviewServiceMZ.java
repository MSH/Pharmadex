/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.msh.pharmadex.dao.CustomReviewDAO;
import org.msh.pharmadex.dao.ReviewQDAO;
import org.msh.pharmadex.dao.iface.RevDeficiencyDAO;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.dao.iface.ReviewDetailDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.RevDeficiency;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.ReviewDetail;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.ReviewQuestion;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.product.ReviewInfoTable;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Author: dudchenko
 */
@Service
public class ReviewServiceMZ implements Serializable {

	@Autowired
	ReviewService reviewService;
	
	@Autowired
	private RevDeficiencyDAO revDeficiencyDAO;
	
	@Autowired
	private UserAccessService userAccessService;
	
/*	@Autowired
	TimelineService timelineService;
	@Autowired
	private ReviewDAO reviewDAO;
	@Autowired
	private GlobalEntityLists globalEntityLists;
	@Autowired
	private ChecklistService checklistService;
	@Autowired
	private ReviewInfoDAO reviewInfoDAO;
	@Autowired
	private ReviewQDAO reviewQDAO;
	@Autowired
	private ReviewDetailDAO reviewDetailDAO;
	@Autowired
	private ProdApplicationsService prodApplicationsService;
	@Autowired
	private RevDeficiencyDAO revDeficiencyDAO;
	@Autowired
	private CustomReviewDAO customReviewDAO;
	@Autowired
	private UserAccessService userAccessService;

	@Autowired
	private UserService userService;

	@Autowired
	private UtilsByReports utilsByReports;*/

	/**
	 * Procedure calculates and saves review status in depends of current status and user role
	 * Then saves review info into database
	 * @param reviewInfo
	 * @param reviewComment
	 * @param userID
	 * @return
	 */
	public RetObject submitReviewInfo(ReviewInfo reviewInfo, ReviewComment reviewComment, Long userID) {
		if (reviewComment.getRecomendType() == null) {
			reviewComment.setFinalSummary(false);
		} else {
			if (reviewComment.getRecomendType().equals(RecomendType.RECOMENDED) || reviewComment.getRecomendType().equals(RecomendType.NOT_RECOMENDED)
					|| reviewComment.getRecomendType().equals(RecomendType.FIR)) {
				if (userAccessService.getWorkspace().isSecReview()) {
					if (userID.equals(reviewInfo.getReviewer().getUserId()))
						if (reviewInfo.isSecreview()) {
							reviewInfo.setReviewStatus(ReviewStatus.SEC_REVIEW);
						} else {
							reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
						}
					else if (userID.equals(reviewInfo.getSecReviewer().getUserId()))
						reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
				} else {
					reviewInfo.setReviewStatus(ReviewStatus.ACCEPTED);
				}
				reviewComment.setFinalSummary(true);
			}else if (reviewComment.getRecomendType().equals(RecomendType.REGISTER)||reviewComment.getRecomendType().equals(RecomendType.SUSPEND)||reviewComment.getRecomendType().equals(RecomendType.CANCEL)){
				reviewInfo.setReviewStatus(ReviewStatus.SUBMITTED);
			}
		}
		reviewInfo.setSubmitDate(new Date());
		reviewInfo.getReviewComments().add(reviewComment);

		List<RevDeficiency> revDeficiencies = revDeficiencyDAO.findByReviewInfo_Id(reviewInfo.getId());
		for (RevDeficiency revDeficiency : revDeficiencies) {
			if (!revDeficiency.isResolved()) {
				return new RetObject("close_def");
			}
		}
		List<ReviewComment> reviewComments = reviewInfo.getReviewComments();
		for (ReviewComment rc : reviewComments) {
			if (rc.getRecomendType() != null) {
				reviewInfo.setRecomendType(rc.getRecomendType());
				reviewInfo.setExecSummary(rc.getComment());
			}
		}
		return getReviewService().saveReviewInfo(reviewInfo);
	}

	public ReviewService getReviewService() {
		return reviewService;
	}

	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	
}
