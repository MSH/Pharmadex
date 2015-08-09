package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.SRA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface SraDAO extends JpaRepository<SRA, Long> {

    List<SRA> findByCountry(String country);


}
