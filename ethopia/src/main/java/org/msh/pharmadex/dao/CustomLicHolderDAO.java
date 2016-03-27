package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.AgentInfo;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.enums.AgentType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by utkarsh on 1/8/15.
 */
@Repository
public class CustomLicHolderDAO {

    @PersistenceContext
    EntityManager entityManager;

    public List<Applicant> findApplicantsByLicHolderAvailability() {
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

        if (agentInfos.size() > 0)
            return false;
        else
            return true;
    }

    public Applicant findFirstAgent(Long id) {
        Applicant applicant;
        try {
            applicant = (Applicant) entityManager.createQuery("select ai.applicant from AgentInfo ai join ai.applicant where ai.licenseHolder.id = :licHolder " +
                    "and ai.agentType = :agentType " +
                    "and (:currDate between ai.startDate and ai.endDate)")
                    .setParameter("licHolder", id)
                    .setParameter("agentType", AgentType.FIRST)
                    .setParameter("currDate", new Date())
                    .getSingleResult();
        } catch (NoResultException no) {
            applicant = null;
        } catch (Exception ex) {
            applicant = null;
        }
        return applicant;
    }

    public List<LicenseHolder> findAll() {
        List<LicenseHolder> licenseHolders = entityManager.createQuery("select distinct lh from LicenseHolder lh left join fetch lh.address.country left join fetch lh.agentInfos ")
                .getResultList();
        return licenseHolders;

    }

    public LicenseHolder findLicHolderByProduct(Long prodID) {
        List<LicenseHolder> licenseHolders = entityManager.createQuery("select lh from LicenseHolder lh join lh.products p where p.id = :prodID")
                .setParameter("prodID", prodID)
                .getResultList();
        if (licenseHolders != null && licenseHolders.size() > 0)
            return licenseHolders.get(0);
        else
            return null;
    }
    public LicenseHolder findLicHolderByName(String name) {
        List<LicenseHolder> licenseHolders = entityManager.createQuery("select lh from LicenseHolder lh join lh.products p where lh.name = :nn")
                .setParameter("nn", name)
                .getResultList();
        if (licenseHolders != null && licenseHolders.size() > 0)
            return licenseHolders.get(0);
        else
            return null;
    }
}
