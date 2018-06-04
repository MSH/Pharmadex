package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.AgentInfo;
import org.msh.pharmadex.domain.POrderDoc;
import org.msh.pharmadex.domain.enums.AgentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface POrderDocDAO extends JpaRepository<POrderDoc, Long> {

    public List<POrderDoc> findByPipOrder_Id(Long id);

    public List<POrderDoc> findByPurOrder_Id(Long id);
}
