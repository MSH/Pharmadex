package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.LicenseHolder;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.service.ProdAppChecklistService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by usrivastava on 01/14/2015.
 */
@ManagedBean
@ViewScoped
public class RegHomeMbeanET implements Serializable{

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{regHomeMbean}")
    private RegHomeMbean regHomeMbean;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{checklistService}")
    private ChecklistService checklistService;

    @ManagedProperty(value = "#{prodAppChecklistService}")
    private ProdAppChecklistService prodAppChecklistService;

    private LicenseHolder licenseHolder;

    @PostConstruct
    private void init() {
        ProdAppInit prodAppInit = userSession.getProdAppInit();
        if (prodAppInit != null) {
            ProdApplications prodApplications = regHomeMbean.getProdApplications();
            prodApplications.setProdAppType(prodAppInit.getProdAppType());
            prodApplications.setSra(prodAppInit.isSRA());
            prodApplications.setFastrack(prodAppInit.isEml());
            prodApplications.setFeeAmt(prodAppInit.getFee());
            prodApplications.setPrescreenfeeAmt(prodAppInit.getPrescreenfee());

            if(prodApplications.getId()==null) {
                List<ProdAppChecklist> prodAppChecklists = prodAppChecklistService.findProdAppChecklistByProdApp(prodApplications.getId());
                if (prodAppChecklists == null || prodAppChecklists.size() < 1) {
                    prodAppChecklists = new ArrayList<ProdAppChecklist>();
//                    prodApplications.setProdAppChecklists(prodAppChecklists);
                    List<Checklist> allChecklist = checklistService.getETChecklists(prodApplications, prodApplications.isSra());
                    ProdAppChecklist eachProdAppCheck;
                    if (allChecklist != null && allChecklist.size() > 0) {
                        for (int i = 0; allChecklist.size() > i; i++) {
                            eachProdAppCheck = new ProdAppChecklist();
                            eachProdAppCheck.setChecklist(allChecklist.get(i));
                            eachProdAppCheck.setProdApplications(prodApplications);
                            prodAppChecklists.add(eachProdAppCheck);
                        }
                    }
//                    prodApplications.setProdAppChecklists(prodAppChecklists);
                }
            }

        }
    }

    public LicenseHolder getLicenseHolder() {
        if(licenseHolder==null){
            List<LicenseHolder> licenseHolders = licenseHolderService.findLicHolderByApplicant(regHomeMbean.getApplicant().getApplcntId());
            licenseHolder = licenseHolders.get(0);
        }
        return licenseHolder;
    }

    public void setLicenseHolder(LicenseHolder licenseHolder) {
        this.licenseHolder = licenseHolder;
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public RegHomeMbean getRegHomeMbean() {
        return regHomeMbean;
    }

    public void setRegHomeMbean(RegHomeMbean regHomeMbean) {
        this.regHomeMbean = regHomeMbean;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }


    public ChecklistService getChecklistService() {
        return checklistService;
    }

    public void setChecklistService(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }
}
