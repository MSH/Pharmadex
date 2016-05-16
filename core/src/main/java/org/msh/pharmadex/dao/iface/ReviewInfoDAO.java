/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.ReviewInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */

public interface ReviewInfoDAO extends JpaRepository<ReviewInfo, Long> {

    public ReviewInfo findByReviewer_UserIdAndProdApplications_Id(Long reviewer_UserId, Long prodApplications_Id);
    //public List<ReviewInfo> findByProdApplications_IdAndReviewer_UserId( Long prodApplications_Id,Long reviewer_UserId);

    @Query("select distinct ri from ReviewInfo ri left join fetch ri.reviewComments  where ri.id = ?1 ")
    public ReviewInfo findById(Long revInfoID);

    public List<ReviewInfo> findByProdApplications_IdOrderByAssignDateAsc(Long id);

    @Query("select r from ReviewInfo r left join fetch r.reviewComments where r.prodApplications.id=?1 and (r.reviewer.userId = ?2 or r.secReviewer.userId = ?3)")
    public List<ReviewInfo> findByProdApplications_IdAndReviewer_UserIdOrSecReviewer_UserId(Long prodApplications_Id, Long reviewer_UserId, Long secReviewer_UserId);

    @Query("select r from ReviewInfo r left join fetch r.reviewComments where r.prodApplications.id=?1 and (r.reviewer.userId = ?2)")
    public List<ReviewInfo> findByProdApplications_IdAndReviewer_UserId(Long prodApplications_Id, Long reviewer_UserId);

    @Query("select rc from ReviewComment rc left join fetch rc.user where rc.reviewInfo.id = ?1 ")
    List<ReviewComment> findReviewComments(Long id);
}

