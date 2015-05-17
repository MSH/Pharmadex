package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.domain.enums.AmdmtState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface POrderDAO extends JpaRepository<POrderBase, Long> {

    public List<POrderBase> findByState(AmdmtState state);

    public List<POrderBase> findByCreatedBy_userIdOrApplicant_applcntId(Long userId, Long applcntId);
}
