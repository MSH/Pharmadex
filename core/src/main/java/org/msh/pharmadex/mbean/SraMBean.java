package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.SRA;
import org.msh.pharmadex.domain.enums.SraType;
import org.msh.pharmadex.service.SraService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RetObject;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/12/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class SraMBean implements Serializable {
    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    @ManagedProperty(value = "#{sraService}")
    private SraService sraService;
    private SRA sra;
    private List<SRA> sras;
    private boolean displayAgency = false;
    private boolean edit = false;

    @PostConstruct
    private void init() {
        sras = sraService.findAllSRAs();
        sra = new SRA();
    }

    public void sratypechange() {
        if (sra != null) {
            if (sra.getSraType().equals(SraType.AGENCY)) {
                displayAgency = true;
            } else if (sra.getSraType().equals(SraType.COUNTRY)) {
                displayAgency = false;
            }
        }

    }

    public void saveSRA() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        sra.setCountry(sra.getCountry().trim());
        sra.setCreatedBy(userService.findUser(userSession.getLoggedINUserID()));
        if (sra.getCountry().equals("")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("requiredvalue")));
        }

        RetObject retObject = sraService.newSRA(sra);
        if (retObject.getMsg().equals("persist")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
            sras = null;
        } else if (retObject.getMsg().equals("exists")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("valid_user_exist")));
        }
    }

    public void updateSRA() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        sra.setCountry(sra.getCountry().trim());
        sra.setUpdatedBy(userService.findUser(userSession.getLoggedINUserID()));
        if (sra.getCountry().equals("")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("requiredvalue")));
        }

        RetObject retObject = sraService.updateSRA(sra);
        if (retObject.getMsg().equals("persist")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
            sras = null;

        }

    }

    public void removeSra(SRA sra) {
        facesContext = FacesContext.getCurrentInstance();
        sraService.deleteSRA(sra);
        sras = null;
        facesContext.addMessage(null, new FacesMessage(bundle.getString("is_deleted")));
    }

    public void initAdd() {
        sra = new SRA();
        edit = false;
    }

    public void initUpdate(SRA updatesra) {
        edit = true;
        sra = updatesra;
        if (sra != null) {
            if (sra.getSraType().equals(SraType.AGENCY)) {
                displayAgency = true;
            } else if (sra.getSraType().equals(SraType.COUNTRY)) {
                displayAgency = false;
            }
        }
    }

    public void cancelSRA() {
        sra = new SRA();
    }

    public SraService getSraService() {
        return sraService;
    }

    public void setSraService(SraService sraService) {
        this.sraService = sraService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public List<SRA> getSras() {
        if (sras == null)
            sras = sraService.findAllSRAs();
        return sras;
    }

    public void setSras(List<SRA> sras) {
        this.sras = sras;
    }

    public SRA getSra() {
        return sra;
    }

    public void setSra(SRA sra) {
        this.sra = sra;
    }

    public boolean isDisplayAgency() {
        return displayAgency;
    }

    public void setDisplayAgency(boolean displayAgency) {
        this.displayAgency = displayAgency;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
}
