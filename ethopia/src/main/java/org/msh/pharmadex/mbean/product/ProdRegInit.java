/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.AgentType;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.msh.pharmadex.util.Scrooge;
import org.msh.pharmadex.utils.Tools;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdRegInit implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    @ManagedProperty(value = "#{checklistService}")
    private ChecklistService checklistService;

    @ManagedProperty(value = "#{productService}")
    ProductService productService;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{productDAO}")
    private ProductDAO productDAO;

    private String[] selSRA;
    private boolean eml = false;
    private boolean displayfeepanel=false;
    private String fee;
    private String prescreenfee;
    private String totalfee;
    private ProdAppType prodAppType;
    private List<ProdAppType> prodAppTypes;
    private FacesContext context;
    private boolean eligible;
    private List<Checklist> checklists;
    private LicenseHolder selLicHolder;
    private List<LicenseHolder> licenseHolders;
    private boolean showProductChoice;
    private boolean showVariationType;
    private ProdTable prodTable;
    //private Product selProduct;
    private int minorQuantity=0;
    private int majorQuantity=0;
    private String varSummary;
    private java.util.ResourceBundle bundle;
    private User curUser;
    private boolean fewLicHolders;
    private boolean standardProcedureRecall = true;

    @PostConstruct
    public void init() {
        if (Scrooge.beanParam("standardProcedure")!=null) standardProcedureRecall = false;
        if (standardProcedureRecall) {// standard procedure - user open registration form
            licenseHolders = licenseHolderService.findLicHolderByApplicant(userSession.getApplcantID());
            if (licenseHolders != null && licenseHolders.size() == 1) {
                selLicHolder = licenseHolders.get(0);
                fewLicHolders = false;
            } else {
                fewLicHolders = true;
            }
            prodAppTypes = new ArrayList<ProdAppType>();
            prodAppTypes.add(ProdAppType.GENERIC);
            prodAppTypes.add(ProdAppType.GENERIC_NO_BE);
            prodAppTypes.add(ProdAppType.NEW_CHEMICAL_ENTITY);
            prodAppTypes.add(ProdAppType.RENEW);
            prodAppTypes.add(ProdAppType.VARIATION);

            prodTable = null;
        }else{ //procedure called from suspention or registration form for the chossen product
            Long prodId = Scrooge.beanParam("prodID");
            Long appId = Scrooge.beanParam("appID");
            if ((prodId==null)||(appId==null)){ //if something wrong, make it manually
                Scrooge.setBeanParam("StandardProcedure", (long) 0);
                init();
            }
            ProdApplications prodApp = prodApplicationsService.findProdApplications(appId);
            Product product = productDAO.findProduct(prodId);
            prodTable = createProdTableRecord(product);
            fewLicHolders = false;
            selLicHolder = licenseHolderService.findLicHolderByProduct(prodId);
        }
        curUser = getUserSession().getUserService().findUser(userSession.getLoggedINUserID());
    }


    public List<LicenseHolder> completeLicHolderList(String query) {
        return JsfUtils.completeSuggestions(query, licenseHolders);
    }

    /**
     * sums minor and major validation fees (depence of number of changes)
     */
    private void addFee() {
        Pattern feePattern = Pattern.compile("^\\$(([1-9]\\d{0,2}(,\\d{3})*)|(([1-9]\\d*)?\\d))([\\.\\,]\\d?\\d?)?$?");
        Matcher feeStr;
        Double numFee = 0.0;
        Double numPreScrFee = 0.0;
        if (majorQuantity > 0){
            calculate("VARIATION_MAJOR");
            feeStr = feePattern.matcher(fee);
            if (!("".equals(fee) || "0".equals(fee))) {
                if (feeStr.matches())
                    numFee = Tools.currencyToDouble(fee) * majorQuantity;
            }
            if (!("".equals(prescreenfee) || "0".equals(prescreenfee))) {
                numPreScrFee = Tools.currencyToDouble(prescreenfee);
            }
        }
        if (minorQuantity > 0){
            calculate("VARIATION_MINOR");
            feeStr = feePattern.matcher(fee);
            if (!("".equals(fee) || "0".equals(fee))) {
                Double numFee2=0.0;
                if (feeStr.matches())
                    numFee2 = Tools.currencyToDouble(fee) * minorQuantity;
                numFee = numFee + numFee2;
            }
            if (!("".equals(fee) || "0".equals(fee))) {
                Double numPreScrFee2 = Tools.currencyToDouble(prescreenfee);
                numPreScrFee = numPreScrFee2 + numPreScrFee;
            }
        }
        if ((numFee+numPreScrFee)>0){
            fee = String.valueOf(numFee.doubleValue());
            prescreenfee = String.valueOf(numPreScrFee.doubleValue());
            Double total = numFee + numPreScrFee;
            totalfee = String.valueOf(total.doubleValue());
        }
    }

    public void calculate(String prodAppTypeName) {
        totalfee="0"; fee="0";prescreenfee="0";
        for (FeeSchedule feeSchedule : globalEntityLists.getFeeSchedules()) {
            if (feeSchedule.getAppType().equals(prodAppTypeName)) {
                totalfee = feeSchedule.getTotalFee();
                fee = feeSchedule.getFee();
                prescreenfee = feeSchedule.getPreScreenFee();
                break;
            }
        }
    }

    public void checkAndCalculate() {
        context = FacesContext.getCurrentInstance();
        if (prodAppType==null) {
            context.addMessage(null, new FacesMessage("prodapptype_null"));
            displayfeepanel = false;
            return;
        } else if (prodAppType==ProdAppType.RENEW){
            if (this.prodTable==null) {
                String errString = getBundle().getString("variationProductRequired");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,errString,""));
                displayfeepanel = false;
                return;
            }
        } else if (prodAppType==ProdAppType.VARIATION) {
            if (this.prodTable==null) {
                String errString = getBundle().getString("variationProductRequired");
                context.addMessage(null, new FacesMessage(errString));
                displayfeepanel = false;
                return;
            }
            if ((getMajorQuantity()==0) && (minorQuantity==0)){
                String errString = getBundle().getString("variationQuantityNotBeNull");
                context.addMessage(null, new FacesMessage(errString));
                displayfeepanel = false;
                return;
            }else{
                addFee();
                populateChecklist();
                displayfeepanel = true;
                //RequestContext.getCurrentInstance().update("additionalPanel");
                return;
            }
        }
        calculate(prodAppType.name());
        populateChecklist();
        displayfeepanel = ! displayfeepanel;
        //RequestContext.getCurrentInstance().update("additionalPanel");
    }

    public void populateChecklist() {
        ProdApplications prodApplications = new ProdApplications();
        prodApplications.setProdAppType(prodAppType);
        if (prodAppType.equals(ProdAppType.VARIATION)){
            List<Checklist> mjChecklists=null;
            List<Checklist> mnChecklists=null;
            if (this.getMinorQuantity()==0&&this.getMajorQuantity()==0) {
                checklists = null;
            }else if (this.getMinorQuantity()>0&&this.getMajorQuantity()==0){
                checklists = checklistService.getETChecklists(prodApplications, false);
            }
            else if (this.getMajorQuantity()>0&&this.getMinorQuantity()==0) {
                checklists = checklistService.getETChecklists(prodApplications, true);
            }else {
                checklists = checklistService.getETChecklists(prodApplications, false);
                mjChecklists = checklistService.getETChecklists(prodApplications, true);
                if (mjChecklists != null && checklists != null) {
                    for (Checklist ch : mjChecklists) {
                        checklists.add(ch);
                    }
                }
            }
        }else if (prodAppType.equals(ProdAppType.RENEW)){
            checklists = checklistService.getChecklists(prodApplications,true);
        }else{
            if (selSRA.length > 0)
                prodApplications.setSra(true);
            else
                prodApplications.setSra(false);
            checklists = checklistService.getChecklists(prodApplications,true);
        }
    }

    public String regApp() {
        checkAndCalculate();
        //if (!displayfeepanel) return "";
        ProdAppInit prodAppInit = new ProdAppInit();
        prodAppInit.setEml(eml);
        prodAppInit.setProdAppType(prodAppType);
        prodAppInit.setSelSRA(selSRA);
        prodAppInit.setFee(fee);
        prodAppInit.setPrescreenfee(prescreenfee);
        prodAppInit.setTotalfee(totalfee);
        prodAppInit.setSRA(selSRA.length > 0);
        prodAppInit.setMjVarQnt(majorQuantity);
        prodAppInit.setMnVarQnt(minorQuantity);
        prodAppInit.setVarSummary(varSummary);
        if (selLicHolder != null) {
            prodAppInit.setLicHolderID(selLicHolder.getId());
            selLicHolder = licenseHolderService.findLicHolder(selLicHolder.getId());
            if (selLicHolder.getAgentInfos() != null && selLicHolder.getAgentInfos().size() > 0) {
                for (AgentInfo agentInfo : selLicHolder.getAgentInfos()) {
                    if (agentInfo.getAgentType().equals(AgentType.FIRST)) {
                        if (agentInfo.getApplicant() != null && agentInfo.getApplicant().getUsers() != null && agentInfo.getApplicant().getUsers().size() > 0) {
                            userSession.setProdAppInit(prodAppInit);
                            if (prodAppType==ProdAppType.RENEW){
                                Long prodAppId = prodTable.getProdAppID();
                                return startReregVar(ProdAppType.RENEW,prodAppId,prodAppInit);
                            }else if (prodAppType==ProdAppType.VARIATION){
                                Long prodAppId = prodTable.getProdAppID();
                                return startReregVar(ProdAppType.VARIATION,prodAppId,prodAppInit);
                            }else
                                return "/secure/prodreghome";
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "No User associated with the local agent representing " + selLicHolder.getName(),
                                    "No User associated with the local agent representing " + selLicHolder.getName()));
                            return "";
                        }
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "No Local Agent associated with " + selLicHolder.getName(),
                        "No Local Agent associated with " + selLicHolder.getName()));
                return "";
            }
        } else {
            userSession.setProdAppInit(prodAppInit);
            return "/secure/prodreghome";

        }
        return "";

    }


    public String startReregVar(ProdAppType newtype, Long parentAppId, ProdAppInit paInit){
        //create copy of inital application and product
        ProdApplications prodAppRenew;
        ProdApplications prodApp = prodApplicationsService.findProdApplications(parentAppId);
        prodApp.setMjVarQnt(paInit.getMjVarQnt());
        prodApp.setMnVarQnt(paInit.getMnVarQnt());
        prodAppRenew=clone(prodApp,newtype,false);
        prodAppRenew.setProdAppDetails(paInit.getVarSummary());
        prodAppRenew.setPrescreenfeeAmt(paInit.getPrescreenfee());
        prodAppRenew.setFeeAmt(paInit.getFee());
        prodAppRenew.setFeeReceived(false);
        prodAppRenew.setFeeSubmittedDt(null);
        prodAppRenew.setBankName(null);
        prodAppRenew.setPrescreenBankName(null);
        prodAppRenew.setDosRecDate(null);
        prodAppRenew.setRegExpiryDate(null);
        prodAppRenew.setProdRegNo(null);
        prodAppRenew.setProdAppNo(null);
        prodAppRenew.setFeeReceipt(null);
        prodAppRenew.setReceiptNo(null);
        prodAppRenew.setPrescreenReceiptNo(null);
        prodAppRenew = prodApplicationsService.saveApplication(prodAppRenew,curUser.getUserId());

        Long prodAppId = prodAppRenew.getId();
        context.getExternalContext().getFlash().put("prodAppID",prodAppId);
        context.getExternalContext().getFlash().put("parentAppId",parentAppId);
        return "/secure/prodreghome.xhtml";
    }

    private ProdApplications clone(ProdApplications src, ProdAppType type, boolean isMajor) {
        ProdApplications paNew = new ProdApplications();
        paNew.setDosRecDate(src.getDosRecDate());
        Long parentProdId = src.getProduct().getId();
        Scrooge.copyData(src, paNew);
        paNew.setId((long) 0);
        paNew.setActive(false);
        paNew.setProdAppType(type);
        paNew.setRegState(RegState.SAVED);
        paNew.setProdRegNo("");
        paNew.setProdAppNo("");
        paNew.setRegistrationDate(null);
        Product p = new Product();
        Product pp = productDAO.findProductEager(parentProdId);
        p.setManufName(pp.getManufName());
        p.setDosUnit(pp.getDosUnit());
        p.setAdminRoute(pp.getAdminRoute());
        p.setAgeGroup(pp.getAgeGroup());
        p.setContType(pp.getContType());
        p.setDosForm(pp.getDosForm());
        p.setDosStrength(pp.getDosStrength());
        p.setApprvdName(null);
        p.setDrugType(pp.getDrugType());
        p.setGenName(pp.getGenName());
        p.setIndications(pp.getIndications());
        p.setNewChemicalEntity(false);
        p.setNewChemicalName(null);
        p.setPackSize(pp.getPackSize());
        p.setPharmacopeiaStds(pp.getPharmacopeiaStds());
        p.setPharmClassif(pp.getPharmClassif());
        p.setDrugType(pp.getDrugType());
        p.setFnm(pp.getFnm());
        p.setIngrdStatment(pp.getIngrdStatment());
        p.setPosology(pp.getPosology());
        p.setProdCategory(pp.getProdCategory());
        p.setProdDesc(pp.getProdDesc());
        p.setProdName(pp.getProdName());
        p.setProdType(pp.getProdType());
        p.setShelfLife(pp.getShelfLife());
        p.setStorageCndtn(pp.getStorageCndtn());
        p.setUseCategories(pp.getUseCategories());
        paNew.setProduct(p);

        List<Atc> atcs = pp.getAtcs();
        List<Atc> atcsNew = null;
        if (atcs != null && atcs.size() > 0){
            atcsNew = new ArrayList<Atc>();
            for (int i = 0; i < atcs.size(); i++) {
                Atc atc = new Atc();
                Atc exist = atcs.get(i);
                Scrooge.copyData(exist, atc);
                atcsNew.add(atc);
            }
        }
        p.setAtcs(atcsNew);

        List<ProdCompany> cmpns = pp.getProdCompanies();
        List<ProdCompany> cmpnsNew=null;
        if (cmpns!=null&&cmpns.size()>0){
            cmpnsNew = new ArrayList<ProdCompany>();
            for(int i=0;i<cmpns.size();i++){
                ProdCompany company = new ProdCompany();
                Scrooge.copyData(cmpns.get(i),company);
                company.setProduct(p);
                cmpnsNew.add(company);
            }
        }
        p.setProdCompanies(cmpnsNew);

        List<ProdExcipient> excs = pp.getExcipients();
        List<ProdExcipient> excsNew=new ArrayList<ProdExcipient>();
        if (excs!=null&&excs.size()>0){
            for(int i=0;i<excs.size();i++){
                ProdExcipient exc = excs.get(i);
                ProdExcipient excNew = new ProdExcipient();
                Scrooge.copyData(exc,excNew);
                excNew.setProduct(p);
                excNew.setId(null);
                excsNew.add(excNew);
            }
        }
        p.setExcipients(excs);

        List<ProdInn> inns = pp.getInns();
        List<ProdInn> innsNew=new ArrayList<ProdInn>();
        if (inns!=null&&inns.size()>0) {
            for(int i=0; i<inns.size();i++){
                ProdInn inn = inns.get(i);
                ProdInn innNew = new ProdInn();
                Scrooge.copyData(inn,innNew);
                innNew.setId(null);
                innNew.setProduct(p);
                innsNew.add(innNew);
            }
        }
        p.setInns(innsNew);

        return paNew;
    }

    private Long getParam(String parameter){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String prodAppId=null;
        if (facesContext.getExternalContext().getFlash()!=null)
            prodAppId = (String) facesContext.getExternalContext().getFlash().get(parameter);
        if (prodAppId==null){
            if (facesContext.getExternalContext().getRequestParameterMap()!=null)
                prodAppId = facesContext.getExternalContext().getRequestParameterMap().get(parameter);
        }
        if (prodAppId!=null){
            return Long.parseLong(prodAppId);
        }
        return null;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public String[] getSelSRA() {
        return selSRA;
    }

    public void setSelSRA(String[] selSRA) {
        this.selSRA = selSRA;
    }

    public boolean isEml() {
        return eml;
    }

    public void setEml(boolean eml) {
        this.eml = eml;
    }

    public boolean isDisplayfeepanel() {
        return displayfeepanel;
    }

    public void setDisplayfeepanel(boolean displayfeepanel) {
        this.displayfeepanel = displayfeepanel;
    }

    public String getTotalfee() {
        return totalfee;
    }

    public void setTotalfee(String totalfee) {
        this.totalfee = totalfee;
    }

    public ProdAppType getProdAppType() {
        return prodAppType;
    }

    public void setProdAppType(ProdAppType prodAppType) {
        this.prodAppType = prodAppType;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public boolean isEligible() {
        if (userSession.isAdmin() || userSession.isHead() || userSession.isStaff())
            eligible = true;

        if (userSession.isCompany()) {
            if (userSession.getApplcantID() == null)
                eligible = false;
            else {
                List<LicenseHolder> licenseHolders = licenseHolderService.findLicHolderByApplicant(userSession.getApplcantID());
                if (licenseHolders != null && licenseHolders.size() > 0)
                    eligible = true;
                else
                    eligible = false;
            }
        }
        return eligible;
    }

    public void ajaxListener(AjaxBehaviorEvent event){
        isShowProductChoice();
        isShowVariationType();
        this.prodTable=null;
        this.majorQuantity=0;
        this.minorQuantity=0;
        if (fewLicHolders)
            this.selLicHolder=null;
        displayfeepanel=false;
        RequestContext.getCurrentInstance().update("reghome");
    }

    public List<ProdTable> completeRegisteredProduct(String query) {
        List<ProdTable> suggestions = new ArrayList<ProdTable>();
        List<ProdTable> prods = productService.findAllRegisteredProduct();
        for (ProdTable p : prods) {
            if ((p.getProdName() != null && p.getProdName().toLowerCase().startsWith(query))
                    || (p.getGenName() != null && p.getGenName().toLowerCase().startsWith(query)))
                    suggestions.add(p);
        }
        return suggestions;
    }

    private ProdApplications getLastProductApplication(Product p){
        List<ProdApplications> prodApps = p.getProdApplicationses();
        if (prodApps==null) return null;
//        if (prodApps.size()==1) return prodApps.get(0);
        for(ProdApplications pa:prodApps){
            if (pa.getRegState().equals(RegState.REGISTERED))
                return pa;
        }
        return null;
    }

    private ProdTable createProdTableRecord(Product p){
        ProdTable pt = new ProdTable();
        pt.setId(p.getId());
        pt.setManufName(p.getManufName());
        pt.setGenName(p.getGenName());
        pt.setProdName(p.getProdName());
        pt.setProdCategory(p.getProdCategory());
        List<ProdApplications> apps = p.getProdApplicationses();
        ProdApplications pa = apps.get(0);
        pt.setProdAppID(pa.getId());
        pt.setRegDate(pa.getRegistrationDate());
        pt.setRegExpiryDate(pa.getRegExpiryDate());
        pt.setRegNo(pa.getProdRegNo());
        pt.setProdDesc(p.getProdDesc());
        pt.setAppName(pa.getApplicant().getAppName());
        return pt;
    }

    public List<ProdTable> completeProduct(String query) {
        List<ProdTable> suggestions = new ArrayList<ProdTable>();
        List<ProdTable> prods;
        if (getSelLicHolder() == null){
            prods = productService.findAllRegisteredProduct();
            return prods;
        }
        Long lcId = getSelLicHolder().getId();
        LicenseHolder lc = licenseHolderService.findLicHolder(lcId);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_YEAR,120);
        List<Product> products = lc.getProducts();
        if (products!=null){
           for (Product p : products) {
               ProdApplications pa = getLastProductApplication(p);
               if (pa!=null){
                   if (prodAppType == ProdAppType.RENEW){
                       //include product to list only if expiration time have not came and no more 120 days before
                        if (pa.getRegExpiryDate()!=null)
                            if (!(pa.getRegExpiryDate().before(minDate.getTime())&&(pa.getRegExpiryDate().after(Calendar.getInstance().getTime()))))
                                continue;
                   }else if (prodAppType == ProdAppType.VARIATION){
                       //if product is expired - ommit it, check next
                       if (pa.getRegExpiryDate()!=null)
                           if (pa.getRegExpiryDate().before(Calendar.getInstance().getTime()))
                               continue;
                   }
                   if (" ".equals(query))
                       suggestions.add(createProdTableRecord(p));
                   else {
                       if (p.getProdName() != null) {
                           if (p.getProdName().toLowerCase().startsWith(query)) {
                               suggestions.add(createProdTableRecord(p));
                               continue;
                           }
                       }
                       if (p.getGenName() != null) {
                          if (p.getGenName().toLowerCase().startsWith(query)) {
                               suggestions.add(createProdTableRecord(p));

                          }
                       }
                   }
               }
           }
        }
        return suggestions;
    }



    public void onItemSelect(SelectEvent event) {
        if(event.getObject() instanceof ProdTable){
            ProdTable prodTableCh = (ProdTable) event.getObject();
            setProdTable(prodTableCh);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", prodTable.getProdName()));
        }else if(event.getObject() instanceof LicenseHolder){
            LicenseHolder lc = (LicenseHolder) event.getObject();
            setSelLicHolder(lc);
        }
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPrescreenfee() {
        return prescreenfee;
    }

    public void setPrescreenfee(String prescreenfee) {
        this.prescreenfee = prescreenfee;
    }

    public List<Checklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<Checklist> checklists) {
        this.checklists = checklists;
    }

    public ChecklistService getChecklistService() {
        return checklistService;
    }

    public void setChecklistService(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    public LicenseHolder getSelLicHolder() {
        return selLicHolder;
    }

    public void setSelLicHolder(LicenseHolder selLicHolder) {
        this.selLicHolder = selLicHolder;
    }

    public List<ProdAppType> getProdAppTypes() {
        return prodAppTypes;
    }

    public void setProdAppTypes(List<ProdAppType> prodAppTypes) {
        this.prodAppTypes = prodAppTypes;
    }

    public List<LicenseHolder> getLicenseHolders() {
        return licenseHolders;
    }

    public void setLicenseHolders(List<LicenseHolder> licenseHolders) {
        this.licenseHolders = licenseHolders;
    }

    public boolean isShowProductChoice() {
        //showProductChoice = true;
        showProductChoice = (prodAppType==ProdAppType.VARIATION) || (prodAppType==ProdAppType.RENEW);
        return showProductChoice;
    }

    public void setShowProductChoice(boolean showProductChoice) {
        this.showProductChoice = showProductChoice;
    }

    public boolean isShowVariationType() {
        showVariationType = (prodAppType==ProdAppType.VARIATION);
        return showVariationType;
    }

    public void setShowVariationType(boolean showVariationType) {
        this.showVariationType = showVariationType;
    }

    public ProdTable getProdTable() {
        return prodTable;
    }

    public void setProdTable(ProdTable prodTable) {
        this.prodTable = prodTable;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public int getMinorQuantity() {
        return minorQuantity;
    }

    public void setMinorQuantity(int minorQuantity) {
        this.minorQuantity = minorQuantity;
    }

    public int getMajorQuantity() {
        return majorQuantity;
    }

    public void setMajorQuantity(int majorQuantity) {
        this.majorQuantity = majorQuantity;
    }

    public java.util.ResourceBundle getBundle() {
        if (bundle!=null) return bundle;
        bundle = context.getApplication().getResourceBundle(context, "msgs");
        return bundle;
    }

    public void setBundle(java.util.ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public FacesContext getContext() {
        return context;
    }

    public void setContext(FacesContext context) {
        this.context = context;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }

    public boolean isFewLicHolders() {
        return fewLicHolders;
    }

    public void setFewLicHolders(boolean fewLicHolders) {
        this.fewLicHolders = fewLicHolders;
    }

    public String getVarSummary() {
        return varSummary;
    }

    public void setVarSummary(String varSummary) {
        this.varSummary = varSummary;
    }

    public boolean isStandardProcedureRecall() {
        return standardProcedureRecall;
    }

    public void setStandardProcedureRecall(boolean standardProcedureRecall) {
        this.standardProcedureRecall = standardProcedureRecall;
    }
}
