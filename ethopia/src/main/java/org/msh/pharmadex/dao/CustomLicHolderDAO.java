package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.AgentInfo;
import org.msh.pharmadex.domain.Applicant;
import org.springframework.stereotype.Repository;
import sun.management.Agent;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by utkarsh on 1/8/15.
 */
@Repository
public class CustomLicHolderDAO {

    @PersistenceContext
    EntityManager entityManager;

    public List<Applicant> findApplicantsByLicHolderAvailability(){
        return entityManager.createQuery("select a from Applicant a")
                .getResultList();
    }

    public boolean validDate(AgentInfo agentInfo) {
        List<AgentInfo> agentInfos = entityManager.createQuery("select ai from AgentInfo ai where ai.licenseHolder.id = :licHolder " +
                "and ai.agentType = :agentType " +
                "and ((ai.startDate between :startdate and :enddate) or (ai.endDate between :startdate and enddate))")
                .setParameter("licHolder", agentInfo.getLicenseHolder().getId())
                .setParameter("agentType", agentInfo.getAgentType())
                .setParameter("startdate", agentInfo.getStartDate())
                .setParameter("enddate", agentInfo.getEndDate())
                .getResultList();

        if(agentInfos.size()>0)
            return false;
        else
            return true;
    }
}
