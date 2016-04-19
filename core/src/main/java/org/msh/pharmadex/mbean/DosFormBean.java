package org.msh.pharmadex.mbean;

import org.hibernate.exception.ConstraintViolationException;
import org.msh.pharmadex.auth.PassPhrase;
import org.msh.pharmadex.dao.iface.DosageFormDAO;
import org.msh.pharmadex.dao.iface.RoleDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.service.*;
import org.primefaces.model.DualListModel;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: innas
 */
@ManagedBean
@ViewScoped
public class DosFormBean implements Serializable {
    @ManagedProperty(value = "#{dosageFormDAO}")
    DosageFormDAO dosageFormDAO;
    @ManagedProperty(value = "#{dosageFormService}")
    DosageFormService dosageFormService;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    //java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    DosageForm dosForm;
    private List<DosageForm> allForms;
    boolean edit;


    public DosageFormService getDosageFormService() {
        return dosageFormService;
    }

    public void setDosageFormService(DosageFormService dosageFormService) {
        this.dosageFormService = dosageFormService;
    }


    public DosageFormDAO getDosageFormDAO() {
        return dosageFormDAO;
    }

    public void setDosageFormDAO(DosageFormDAO dosageFormDAO) {
        this.dosageFormDAO = dosageFormDAO;
    }

    @PostConstruct
    private void init() {
        allForms= dosageFormService.findAllDosForm();
        dosForm = new DosageForm();
    }

    public DosageForm getDosForm() {
        return dosForm;
    }

    public void setDosForm(DosageForm dosForm) {
        this.dosForm = dosForm;
    }


    public List<DosageForm> getAllForms() {

            return allForms;
    }

    public void setAllForms(List<DosageForm> allForms) {
        this.allForms = allForms;
    }

    public void addForm(){
          dosForm = new DosageForm();
          setEdit(true);

    }

    public String save() {
        facesContext = FacesContext.getCurrentInstance();
        if (dosForm.getUid() == 0)
            dosForm.setUid(null);
        try {
            dosageFormDAO.save(dosForm);
            allForms = null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public void cancel(){
        dosForm=new DosageForm();
    }

    public void onRowSelect() {
        setEdit(true);
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        facesContext.addMessage(null, new FacesMessage("Successful", "Selected " + selectedUser.getName()));
    }


    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public void initUpdate(DosageForm f) {
        dosForm = f;
        edit = true;
    }
}
