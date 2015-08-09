package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.FastTrackMed;
import org.msh.pharmadex.domain.SRA;
import org.msh.pharmadex.service.FastTrackMedService;
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
public class FastTrackMBean implements Serializable {
    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    @ManagedProperty(value = "#{fastTrackMedService}")
    private FastTrackMedService fastTrackMedService;
    private FastTrackMed fastTrackMed;
    private List<FastTrackMed> fastTrackMeds;
    private boolean edit = false;

    @PostConstruct
    private void init() {
        fastTrackMeds = fastTrackMedService.findAll();
        fastTrackMed = new FastTrackMed();
    }

    public void saveFastTrack() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        fastTrackMed.setGenMed(fastTrackMed.getGenMed());
        fastTrackMed.setCreatedBy(userService.findUser(userSession.getLoggedINUserID()));
        if (fastTrackMed.getGenMed().equals("")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("requiredvalue")));
        }

        RetObject retObject = fastTrackMedService.newFastTrackMed(fastTrackMed);
        if (retObject.getMsg().equals("persist")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
            fastTrackMeds = null;
        } else if (retObject.getMsg().equals("exists")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("valid_user_exist")));
        }
    }

    public void updateSRA() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        fastTrackMed.setGenMed(fastTrackMed.getGenMed().trim());
        fastTrackMed.setUpdatedBy(userService.findUser(userSession.getLoggedINUserID()));
        if (fastTrackMed.getGenMed().equals("")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("requiredvalue")));
        }

        RetObject retObject = fastTrackMedService.updateFastTrackMed(fastTrackMed);
        if (retObject.getMsg().equals("persist")) {
            facesContext.addMessage(null, new FacesMessage(bundle.getString("global.success")));
            fastTrackMeds = null;

        }

    }

    public void removeSra(SRA sra) {
        facesContext = FacesContext.getCurrentInstance();
        fastTrackMedService.deleteFastTrack(fastTrackMed);
        fastTrackMeds = null;
        facesContext.addMessage(null, new FacesMessage(bundle.getString("is_deleted")));
    }

    public void initAdd() {
        fastTrackMed = new FastTrackMed();
        edit = false;
    }

    public void initUpdate(FastTrackMed updatesra) {
        fastTrackMed = updatesra;
        edit = true;
    }

    public void cancelFastTrack() {
        fastTrackMed = new FastTrackMed();
    }


    public FastTrackMedService getFastTrackMedService() {
        return fastTrackMedService;
    }

    public void setFastTrackMedService(FastTrackMedService fastTrackMedService) {
        this.fastTrackMedService = fastTrackMedService;
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

    public FastTrackMed getFastTrackMed() {
        return fastTrackMed;
    }

    public void setFastTrackMed(FastTrackMed fastTrackMed) {
        this.fastTrackMed = fastTrackMed;
    }

    public List<FastTrackMed> getFastTrackMeds() {
        if (fastTrackMeds == null)
            fastTrackMeds = fastTrackMedService.findAll();
        return fastTrackMeds;
    }

    public void setFastTrackMeds(List<FastTrackMed> fastTrackMeds) {
        this.fastTrackMeds = fastTrackMeds;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
}
