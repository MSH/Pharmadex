package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PharmacySiteChecklist;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface RxSiteChecklistDAO extends CrudRepository<PharmacySiteChecklist, Long> {

    public List<PharmacySiteChecklist> findByPharmacySite_Id(Long pharmacySite_Id);
}
