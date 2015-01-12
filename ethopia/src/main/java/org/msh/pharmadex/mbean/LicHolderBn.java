package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.UserType;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.util.JsfUtils;
import org.springframework.transaction.annotation.Transactional;
import sun.management.Agent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class LicHolderBn implements Serializable {

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private LicenseHolder licenseHolder;

    private List<AgentInfo> agentInfos;
    private AgentInfo agentInfo;

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    @PostConstruct
    public void init(){
        agentInfo = new AgentInfo();
    }

    @Transactional
    public void addAgent() {
        if (agentInfos == null)
            agentInfos = new ArrayList<AgentInfo>();
        agentInfos.add(agentInfo);
        licenseHolderService.addAgent(agentInfo);
        licenseHolder.setAgentInfos(agentInfos);
        agentInfo = new AgentInfo();

    }

    public void cancelAddAgent(){
        agentInfo = new AgentInfo();

    }

    public void initAgentInfo() {
        agentInfo = new AgentInfo();
    }

    public List<Applicant> completeApplicantList(String query) {
        return JsfUtils.completeSuggestions(query, getAvailableApplicants());
    }

    private List<Applicant> getAvailableApplicants() {
        return licenseHolderService.getAvailableApplicants();
    }


    public LicenseHolder getLicenseHolder() {
        if (licenseHolder == null) {
            if(userSession.getLicHolderID()!=null)
            licenseHolder = licenseHolderService.findLicHolder(userSession.getLicHolderID());
        }
        return licenseHolder;
    }

    public void setLicenseHolder(LicenseHolder licenseHolder) {
        this.licenseHolder = licenseHolder;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<AgentInfo> getAgentInfos() {
        if(agentInfos==null) {
            if (licenseHolder != null)
                agentInfos = licenseHolderService.findAllAgents(licenseHolder.getId());
        }
        return agentInfos;
    }

    public void setAgentInfos(List<AgentInfo> agentInfos) {
        this.agentInfos = agentInfos;
    }

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }
}
