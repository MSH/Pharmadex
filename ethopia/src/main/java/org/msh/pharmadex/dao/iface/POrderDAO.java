package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.POrderBase;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: usrivastava
 */
public interface POrderDAO extends JpaRepository<POrderBase, Long> {

//    public List<POrderBase> findByState(AmdmtState state);

//    public List<POrderBase> findByCreatedBy_userIdOrApplicant_applcntId(Long userId, Long applcntId);
}
