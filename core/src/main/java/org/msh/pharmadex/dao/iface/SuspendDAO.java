package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.SuspDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14. Updated Odissey
 */
public interface SuspendDAO extends JpaRepository<SuspDetail, Long> {

    List<SuspDetail> findByProdApplications_Id(Long prodApplications_Id);

    List<SuspDetail> findByModerator_UserId(Long moderator_UserId);

    List<SuspDetail> findByReviewer_UserId(Long reviewer_UserId);

    List<SuspDetail> findByComplete(boolean complete);

    List<SuspDetail> findByModerator_UserIdAndComplete(Long moderator_UserId,boolean complete);

    List<SuspDetail> findByReviewer_UserIdAndComplete(Long reviewer_UserId,boolean complete);
}
