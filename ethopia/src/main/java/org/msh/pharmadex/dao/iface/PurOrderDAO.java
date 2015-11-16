package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.domain.PurOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: usrivastava
 */
public interface PurOrderDAO extends JpaRepository<PurOrder, Long> {

//    public List<PurOrder> findByState(AmdmtState state);

//    public List<PurOrder> findByCreatedBy_userIdOrApplicant_applcntId(Long userId, Long applcntId);

    public POrderBase findByPipNo(String pipNo);
}
