package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.AgentInfo;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.mbean.NavigationBean;
import org.msh.pharmadex.service.LicenseHolderService;
import org.springframework.web.util.WebUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ResourceBundle;

@ManagedBean
@SessionScoped
public class NavigationBeanET extends NavigationBean implements Serializable {

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @Override
    public String regProductAction() {
        System.out.println("reached ethiopian regProductAction");
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
        if(userSession.isCompany()){
            Applicant app = userSession.getApplicant();
            if(app!=null) {
                LicenseHolder licenseHolder = licenseHolderService.findLicHolderByApplicant(app.getApplcntId());
                if(licenseHolder==null) {
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), "You are not registered as an Agent for any License Holder. Please fill out the Agency Agreement form before registering a Product."));
                    return "";
                }
            }
        }
        return "/secure/prodreghome.faces";
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}