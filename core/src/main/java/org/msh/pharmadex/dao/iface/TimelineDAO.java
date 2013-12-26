package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface TimelineDAO extends JpaRepository<TimeLine, Long> {

    public List<TimeLine> findByProdApplications_IdOrderByStatusDateDesc(Long prodApplications_Id);

}
