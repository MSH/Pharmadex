package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface PIPOrderDAO extends JpaRepository<PIPOrder, Long> {

    public List<PIPOrder> findByState(AmdmtState state);

    public List<PIPOrder> findByCreatedBy_userIdOrApplicant_applcntId(Long userId, Long applcntId);

    public POrderBase findByPipNo(String pipNo);

}
