package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.SuspDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface SuspendDAO extends JpaRepository<SuspDetail, Long> {

    List<SuspDetail> findByProdApplications_Id(Long prodApplications_Id);

    List<SuspDetail> findByModerator_UserId(Long moderator_UserId);

    List<SuspDetail> findByReviewer_UserId(Long reviewer_UserId);
}
