package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.CustomLicHolderDAO;
import org.msh.pharmadex.dao.iface.AgentInfoDAO;
import org.msh.pharmadex.dao.iface.LicenseHolderDAO;
import org.msh.pharmadex.domain.AgentInfo;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.AgentType;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
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
        return customLicHolderDAO.findAll();
    }

    @Transactional
    public LicenseHolder findLicHolder(Long licHolderID) {
        if (licHolderID != null)
            return licenseHolderDAO.findOne(licHolderID);
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
        if (agentInfo == null)
            return "error";
        if (agentInfo.getEndDate().before(agentInfo.getStartDate()))
            return "date_end_before_start";

        if (!customLicHolderDAO.validDate(agentInfo))
            return "licholder_present";

        agentInfo.setCreatedDate(new Date());
        agentInfo = agentInfoDAO.save(agentInfo);
        return "persist";

    }

    public String updateAgent(AgentInfo agentInfo) {
        if (agentInfo == null)
            return "error";
        if (agentInfo.getEndDate().before(agentInfo.getStartDate()))
            return "date_end_before_start";

        agentInfo.setUpdatedDate(new Date());
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
        } catch (Exception ex) {
            return "error";
        }
    }

    public String updateLicHolder(LicenseHolder licenseHolder) {
        try {
            if (licenseHolder == null)
                return "empty";

            licenseHolderDAO.saveAndFlush(licenseHolder);
            return "persist";
        } catch (Exception ex) {
            return "error";
        }
    }

    public List<LicenseHolder> findLicHolderByApplicant(Long applcntId) {
        List<LicenseHolder> licenseHolders = new ArrayList<LicenseHolder>();
        if (applcntId == null) {
            return customLicHolderDAO.findAll();
        }

        List<AgentInfo> agentInfos = agentInfoDAO.findByApplicant_applcntIdAndAgentType(applcntId, AgentType.FIRST);
        if (agentInfos != null) {
            for (AgentInfo agentInfo : agentInfos) {
                if (agentInfo.getStartDate().before(new Date()) && agentInfo.getEndDate().after(new Date()))
                    licenseHolders.add(agentInfo.getLicenseHolder());
            }
        }
        return licenseHolders;
    }

    public String saveProduct(Long applcntId, Product product) {
        try {
            List<LicenseHolder> licenseHolders = findLicHolderByApplicant(applcntId);
            //TODO
            //Replaces this code with the license holder selection done.
            LicenseHolder licenseHolder = licenseHolders.get(0);
            if (licenseHolder.getProducts() == null)
                licenseHolder.setProducts(new ArrayList<Product>());
            licenseHolder.getProducts().add(product);
            licenseHolderDAO.save(licenseHolder);
            return "persist";
        } catch (Exception ex) {
            return "error";
        }

    }

    public RetObject saveProduct(LicenseHolder licenseHolder, Product product) {
        try {
            boolean exist = false;
            licenseHolder = licenseHolderDAO.findOne(licenseHolder.getId());
            if (licenseHolder.getProducts() == null)
                licenseHolder.setProducts(new ArrayList<Product>());
            else {
                List<Product> products = licenseHolder.getProducts();
                if (products != null) {
                    for (Product p : products) {
                        if (p.getId().equals(product.getId())) {
                            exist = true;
                            break;
                        }
                    }

                } else {
                    exist = false;
                }
            }
            if (!exist) {
                licenseHolder.getProducts().add(product);
                licenseHolder = licenseHolderDAO.save(licenseHolder);
            }
            return new RetObject("persist", licenseHolder);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new RetObject("error");
        }

    }

    public AgentInfo findAgentInfoByID(Long id) {
        if (id == null)
            return null;

        return agentInfoDAO.findOne(id);

    }


    public Applicant findApplicantByLicHolder(Long id) {
        if (id == null)
            return null;

        Applicant applicant = customLicHolderDAO.findFirstAgent(id);
        return applicant;
    }

    public LicenseHolder findLicHolderByProduct(Long prodID) {
        if (prodID == null)
            return null;

        LicenseHolder licenseHolder = customLicHolderDAO.findLicHolderByProduct(prodID);
        return licenseHolder;
    }
}
