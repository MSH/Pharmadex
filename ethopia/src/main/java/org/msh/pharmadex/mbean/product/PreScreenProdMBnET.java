package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.RegState;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * Created by utkarsh on 2/23/15.
 */
@ManagedBean
@ViewScoped
public class PreScreenProdMBnET extends PreScreenProdMBn {

    @Override
    public boolean isDisplayScreenAction() {
        if (processProdBn != null && processProdBn.getProdApplications() != null) {
            if (processProdBn.getProdApplications().getRegState().equals(RegState.NEW_APPL) || processProdBn.getProdApplications().getRegState().equals(RegState.FOLLOW_UP))
                displayScreenAction = true;
            else
                displayScreenAction = false;
        }
        return displayScreenAction;
    }

    @Override
    public String completeScreen() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        ProdApplications prodApplications = processProdBn.getProdApplications();
        //prodApplicationsService.updateProdApp(prodApplications,getUserSession().getLoggedINUserID());
        if (!prodApplications.isPrescreenfeeReceived()) {
            facesContext.addMessage(null, new FacesMessage("Pre-screen fees not received"));
            return "";

        }

        return super.completeScreen();
    }


}
