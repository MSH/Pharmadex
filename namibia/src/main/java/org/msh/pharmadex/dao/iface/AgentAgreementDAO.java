package org.msh.pharmadex.dao.iface;

import java.util.List;

import org.msh.pharmadex.domain.AgentAgreement;
import org.msh.pharmadex.domain.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentAgreementDAO extends JpaRepository<AgentAgreement, Long> {
	List<AgentAgreement> findByApplicant(Applicant applicant);

}
