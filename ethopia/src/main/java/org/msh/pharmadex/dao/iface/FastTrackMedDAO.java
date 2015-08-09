package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.FastTrackMed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface FastTrackMedDAO extends JpaRepository<FastTrackMed, Long> {

    List<FastTrackMed> findByGenMedContainingIgnoreCase(String genMed);


}
