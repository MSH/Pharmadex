/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewInfo;
import org.springframework.data.jpa.repository.JpaRepository;

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

    public List<ReviewInfo> findByProdApplications_IdOrderByAssignDateAsc(Long id);

}

