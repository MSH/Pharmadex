package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.TimeLine;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface TimelineDAO extends CrudRepository<TimeLine, Long>{

    public List<TimeLine> findByProdApplications_IdOrderByStatusDateDesc(Long prodApplications_Id);

}
