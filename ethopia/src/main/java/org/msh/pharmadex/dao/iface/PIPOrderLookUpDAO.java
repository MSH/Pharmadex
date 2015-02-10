package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
public interface PIPOrderLookUpDAO extends JpaRepository<PIPOrderLookUp, Long> {


    List<PIPOrderLookUp> findBy();
}
