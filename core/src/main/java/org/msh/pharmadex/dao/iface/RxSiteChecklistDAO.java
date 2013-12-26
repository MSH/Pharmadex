package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PharmacySiteChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface RxSiteChecklistDAO extends JpaRepository<PharmacySiteChecklist, Long> {

    public List<PharmacySiteChecklist> findByPharmacySite_Id(Long pharmacySite_Id);
}
