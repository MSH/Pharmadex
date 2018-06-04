package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.FastTrackMed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface FastTrackMedDAO extends JpaRepository<FastTrackMed, Long> {

    @Query("select f from FastTrackMed f where f.genMed = ?1 and ?2 between f.startDate and f.endDate")
    List<FastTrackMed> findByGenMedAndCurrDate(String genMed, Date currDate);

    List<FastTrackMed> findByGenMed(String genMed);


}
