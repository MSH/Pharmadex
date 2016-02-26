package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.AgentInfo;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.enums.AgentType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface AgentInfoDAO extends JpaRepository<AgentInfo, Long> {

    @Query("select ai from AgentInfo ai where ai.licenseHolder.id = ?1 ")
    List<AgentInfo> findByLicenseHolder_Id(Long id);

    List<AgentInfo> findByApplicant_applcntIdAndAgentType(Long applicantId, AgentType agentType);

}
