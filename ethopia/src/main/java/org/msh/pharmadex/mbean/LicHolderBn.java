package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.context.RequestContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    FacesContext facesContext;
    java.util.ResourceBundle resourceBundle;


    private LicenseHolder licenseHolder;
    private User user;
    private List<AgentInfo> agentInfos;
    private AgentInfo agentInfo;

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    @PostConstruct
    public void init() {
        agentInfo = new AgentInfo();
        user = userService.findUser(userSession.getLoggedINUserID());
    }

    @Transactional
    public void addAgent() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        if (agentInfos == null)
            agentInfos = new ArrayList<AgentInfo>();
        agentInfo.setLicenseHolder(licenseHolder);
        agentInfo.setCreatedBy(user);
        String ret = licenseHolderService.addAgent(agentInfo);
        if (ret.equalsIgnoreCase("persist")) {
            agentInfos.add(agentInfo);
            licenseHolder.setAgentInfos(agentInfos);
            RequestContext.getCurrentInstance().execute("addAgentdlg.hide()");
        } else {
            facesContext.addMessage("Error", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to add Agent", "Unable to add Agent"));
            RequestContext.getCurrentInstance().execute("addAgentdlg.show()");
        }
        agentInfo = new AgentInfo();

    }

    public void cancelAddAgent() {
        agentInfo = new AgentInfo();

    }

    public String addLicHolder() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

        licenseHolder.setCreatedDate(new Date());
        licenseHolder.setCreatedBy(user);
        String ret = licenseHolderService.saveLicHolder(licenseHolder);

        if (ret.equalsIgnoreCase("persist")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("lic_holder_add_success")));
            return "licenseholderlist";
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
            return null;
        }
    }

    public String cancelAddLicHolder(){
        licenseHolder = new LicenseHolder();
        return "licenseholderlist";
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
            if (userSession.getLicHolderID() != null) {
                licenseHolder = licenseHolderService.findLicHolder(userSession.getLicHolderID());
                userSession.setLicHolderID(null);
            }else{
                licenseHolder = new LicenseHolder();
                licenseHolder.setAddress(new Address());
            }
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
        if (agentInfos == null) {
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

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
