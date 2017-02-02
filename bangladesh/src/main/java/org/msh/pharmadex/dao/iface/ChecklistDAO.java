package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Checklist;
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

public interface ChecklistDAO extends JpaRepository<Checklist, Long> {

	List<Checklist> findByGenMedOrderByOrdAsc(boolean b);

	List<Checklist> findByRecognizedMedOrderByOrdAsc(boolean b);

	List<Checklist> findByNewMedOrderByOrdAsc(boolean b);
	
	List<Checklist> findAllByOrderByOrdAsc();

 
}

