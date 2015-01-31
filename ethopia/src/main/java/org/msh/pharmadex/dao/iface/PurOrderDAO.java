package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PIPOrder;
import org.msh.pharmadex.domain.PurOrder;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface PurOrderDAO extends JpaRepository<PurOrder, Long> {

    public List<PurOrder> findByState(AmdmtState state);

    public List<PurOrder> findByCreatedBy_userIdOrApplicant_applcntId(Long userId, Long applcntId);
}
