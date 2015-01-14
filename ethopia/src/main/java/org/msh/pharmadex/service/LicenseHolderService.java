package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.ApplicantDAO;
import org.msh.pharmadex.dao.CustomLicHolderDAO;
import org.msh.pharmadex.dao.iface.AgentInfoDAO;
import org.msh.pharmadex.dao.iface.LicenseHolderDAO;
import org.msh.pharmadex.domain.AgentInfo;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.LicenseHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class LicenseHolderService implements Serializable {

    @Autowired
    private LicenseHolderDAO licenseHolderDAO;

    @Autowired
    private AgentInfoDAO agentInfoDAO;

    @Autowired
    private CustomLicHolderDAO customLicHolderDAO;

    public List<LicenseHolder> findAllLicenseHolder() {
        return licenseHolderDAO.findAll();
    }

    public LicenseHolder findLicHolder(String licHolderID) {
        if(licHolderID!=null)
            return licenseHolderDAO.findOne(Long.valueOf(licHolderID));
        else
            return null;
    }

    public List<AgentInfo> findAllAgents(Long id) {
        return agentInfoDAO.findByLicenseHolder_Id(id);


    }

    public List<Applicant> getAvailableApplicants() {
        List<Applicant> applicants;
        applicants = customLicHolderDAO.findApplicantsByLicHolderAvailability();
        return applicants;

    }

    public String addAgent(AgentInfo agentInfo) {
        if(agentInfo==null)
            return "error";
        if(agentInfo.getEndDate().before(agentInfo.getStartDate()))
            return "date_end_before_start";

        if(!customLicHolderDAO.validDate(agentInfo))
            return "licholder_present";

        agentInfo.setCreatedDate(new Date());
        agentInfo = agentInfoDAO.save(agentInfo);
        return "persist";

    }

    public String saveLicHolder(LicenseHolder licenseHolder) {
        try {
            if (licenseHolder == null)
                return "empty";

            List<LicenseHolder> licenseHolders = licenseHolderDAO.findByName(licenseHolder.getName().trim());

            if (licenseHolders.size() > 0)
                return "duplicate";

            licenseHolderDAO.save(licenseHolder);
            return "persist";
        }catch (Exception ex){
            return "error";
        }
    }
}
