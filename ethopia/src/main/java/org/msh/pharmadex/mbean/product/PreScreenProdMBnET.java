package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.enums.RegState;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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


}
