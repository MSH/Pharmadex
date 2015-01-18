package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PharmacySite;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
public interface PIPOrderDAO extends JpaRepository<PIPOrder, Long> {

    public ArrayList<PIPOrder> findByState(AmdmtState state);


}
