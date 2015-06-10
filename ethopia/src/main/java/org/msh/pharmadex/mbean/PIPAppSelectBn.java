package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.PIPOrderLookUp;
import org.msh.pharmadex.domain.POrderChecklist;
import org.msh.pharmadex.mbean.product.UserDTO;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class PIPAppSelectBn implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(PIPAppSelectBn.class);

    @ManagedProperty(value = "#{pIPOrderBn}")
    PIPOrderBn pipOrderBn;

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{countryService}")
    CountryService countryService;

    @ManagedProperty(value = "#{applicantService}")
    ApplicantService applicantService;

    @ManagedProperty(value = "#{userService}")
    UserService userService;

    protected Applicant selectedApplicant;
    protected org.msh.pharmadex.domain.User applicantUser;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private boolean showApp;
    private boolean showUser;
    private boolean showUserSelect;

    private List<UserDTO> users;
    protected UserDTO selectedUser;

    @Transactional
    public void gmpChangeListener() {
//        if (selectedCompany.isGmpInsp())
//            showGMP = true;
//        else
//            showGMP = false;
        logger.error("inside gmpChangeListener");
        if (selectedApplicant != null && selectedApplicant.getApplcntId() != null) {
            selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
            showApp = true;
            convertUser(selectedApplicant.getUsers());
            if (users.size() > 1) {
                setShowUserSelect(true);
            } else {
                if (users.size() == 1) {
                    selectedUser = users.get(0);
                    showUser = true;
                }
            }
        }

    }

    private void convertUser(List<org.msh.pharmadex.domain.User> users) {
        this.users = new ArrayList<UserDTO>();
        for (org.msh.pharmadex.domain.User u : users) {
            this.users.add(new UserDTO(u));
        }


    }

    public void appChangeListenener(SelectEvent event) {
        logger.error("inside appChangeListenener");
        logger.error("Selected company is " + selectedApplicant.getAppName());
        logger.error("event " + event.getObject());
        gmpChangeListener();


    }

    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("User Selected", ((UserDTO) event.getObject()).getUsername());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        logger.error("Selected User is " + ((UserDTO) event.getObject()).getUsername());
    }

    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Car Unselected", ((Applicant) event.getObject()).getAppName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }


    public void appChangeListenener(AjaxBehaviorEvent event) {
        logger.error("inside appChangeListenener");
//        logger.error("Selected company is " + selectedApplicant.getAppName());
        logger.error("event " + event.getSource());
        gmpChangeListener();


    }

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{POrderService}")
    private POrderService pOrderService;


    @Transactional
    public void addApptoRegistration() {
        selectedApplicant = applicantService.findApplicant(selectedApplicant.getApplcntId());
        if (selectedUser != null)
            applicantUser = userService.findUser(selectedUser.getUserId());
        pipOrderBn.setApplicant(selectedApplicant);
        pipOrderBn.setApplicantUser(applicantUser);
        pipOrderBn.getPipOrder().setCreatedBy(userService.findUser(userSession.getLoggedINUserID()));
        pipOrderBn.getPipOrder().setApplicantUser(applicantUser);

        List<POrderChecklist> pOrderChecklists = new ArrayList<POrderChecklist>();
        List<PIPOrderLookUp> allChecklist = pOrderService.findPIPCheckList(selectedApplicant.getApplicantType(), true);
        POrderChecklist eachCheckList;
        for (int i = 0; allChecklist.size() > i; i++) {
            eachCheckList = new POrderChecklist();
            eachCheckList.setPipOrderLookUp(allChecklist.get(i));
            eachCheckList.setPipOrder(pipOrderBn.getPipOrder());
            pOrderChecklists.add(eachCheckList);
        }
        pipOrderBn.setpOrderChecklists(pOrderChecklists);
    }

    public void cancelAddApplicant() {
        selectedApplicant = new Applicant();
        applicantUser = new org.msh.pharmadex.domain.User();
    }


    public List<Applicant> completeApplicantList(String query) {
        List<Applicant> applicants = applicantService.findAllApplicants();
        return JsfUtils.completeSuggestions(query, applicants);
    }

    public boolean isShowApp() {
        return showApp;
    }

    public void setShowApp(boolean showApp) {
        this.showApp = showApp;
    }

    public boolean isShowUserSelect() {
        return showUserSelect;
    }

    public void setShowUserSelect(boolean showUserSelect) {
        this.showUserSelect = showUserSelect;
    }

    public Applicant getSelectedApplicant() {
        return selectedApplicant;
    }

    public void setSelectedApplicant(Applicant selectedApplicant) {
        this.selectedApplicant = selectedApplicant;
    }

    public org.msh.pharmadex.domain.User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(org.msh.pharmadex.domain.User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public boolean isShowUser() {
        return showUser;
    }

    public void setShowUser(boolean showUser) {
        this.showUser = showUser;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public UserDTO getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(UserDTO selectedUser) {
        this.selectedUser = selectedUser;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public ApplicantService getApplicantService() {
        return applicantService;
    }

    public void setApplicantService(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public PIPOrderBn getPipOrderBn() {
        return pipOrderBn;
    }

    public void setPipOrderBn(PIPOrderBn pipOrderBn) {
        this.pipOrderBn = pipOrderBn;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public POrderService getpOrderService() {
        return pOrderService;
    }

    public void setpOrderService(POrderService pOrderService) {
        this.pOrderService = pOrderService;
    }
}
