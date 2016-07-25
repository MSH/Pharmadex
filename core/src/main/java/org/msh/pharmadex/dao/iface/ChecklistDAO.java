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

    public List<Checklist> findByHeaderOrderByIdAsc(boolean header);

    //Used in Ethiopia
    public List<Checklist> findByRecognizedMedOrderByIdAsc(boolean sra);

    @Query("select c from Checklist c where c.recognizedMed = ?1 or c.module = 'Clinical Review'")
    public List<Checklist> findBySRANewMeds(boolean recognized);

    //Used in Ethiopia
    public List<Checklist> findByNewMedOrderByIdAsc(boolean newMed);

    public List<Checklist> findByGenMedOrderByIdAsc(boolean genMed);

    public List<Checklist> findByRenewalOrderByIdAsc(boolean renewal);

    public List<Checklist> findByHeaderAndRecognizedMed(boolean header, boolean recognizedMed);

    public List<Checklist> findByVariationOrderByIdAsc(boolean variation);
    public List<Checklist> findByMajvarOrderByIdAsc(boolean majvar);
}

