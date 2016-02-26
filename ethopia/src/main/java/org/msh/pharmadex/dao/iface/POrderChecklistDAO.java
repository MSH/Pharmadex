package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.POrderChecklist;
import org.msh.pharmadex.domain.POrderDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface POrderChecklistDAO extends JpaRepository<POrderChecklist, Long> {

    @Query("select c from POrderChecklist c left join fetch c.pipOrderLookUp where c.pipOrder.id = ?1")
    public List<POrderChecklist> findByPipOrder_Id(Long id);

    @Query("select c from POrderChecklist c left join fetch c.pipOrderLookUp where c.purOrder.id = ?1")
    public List<POrderChecklist> findByPurOrder_Id(Long id);
}
