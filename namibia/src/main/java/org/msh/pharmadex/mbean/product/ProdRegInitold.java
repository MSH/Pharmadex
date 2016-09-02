/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.ProductDAO;
import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Checklist;
import org.msh.pharmadex.domain.FeeSchedule;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.service.ApplicantService;
import org.msh.pharmadex.service.ChecklistService;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.util.Scrooge;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProdRegInitold implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{checklistService}")
    private ChecklistService checklistService;
    @ManagedProperty(value = "#{applicantService}")
    ApplicantService applicantService;
    
    @ManagedProperty(value = "#{productService}")
    ProductService productService;

    private String[] selSRA;
    private boolean eml = false;
    private boolean displayfeepanel;
    private String fee;
    private String prescreenfee;
    private String totalfee;
    private ProdAppType prodAppType;
    private FacesContext context;
    private boolean eligible;
    private List<Checklist> checklists;
    private List<ProdAppType> prodAppTypes;
    private boolean showVariationType;
    private boolean showProductChoice;
    private int minorQuantity=0;
    private int majorQuantity=0;
    private ProdTable prodTable;
    private  Applicant applicant;
    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{productDAO}")
    private ProductDAO productDAO;
    private User curUser;
    public boolean isShowProductChoice() {
    	   showProductChoice = (prodAppType==ProdAppType.VARIATION) || (prodAppType==ProdAppType.RENEW);
           return showProductChoice;
	}

	public void setShowProductChoice(boolean showProductChoice) {
		this.showProductChoice = showProductChoice;
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

	
     
    public void calculate() {
        context = FacesContext.getCurrentInstance();
        try {
            if (prodAppType == null) {
                context.addMessage(null, new FacesMessage("prodapptype_null"));
                displayfeepanel = false;
            } else {
                for (FeeSchedule feeSchedule : globalEntityLists.getFeeSchedules()) {
                    if (feeSchedule.getAppType().equals(prodAppType.name())) {
                        totalfee = feeSchedule.getTotalFee();
                        fee = feeSchedule.getFee();
                        prescreenfee = feeSchedule.getPreScreenFee();
                        break;
                    }
                }
                populateChecklist();
                displayfeepanel = true;
            }
        }catch (Exception ex){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));

        }
    }

    private void populateChecklist() {
        ProdApplications prodApplications = new ProdApplications();
        prodApplications.setProdAppType(prodAppType);
        if (selSRA != null && selSRA.length > 0)
            prodApplications.setSra(true);
        else
            prodApplications.setSra(false);
        checklists = checklistService.getChecklists(prodApplications, true);
    }

    public String regApp() {
        ProdAppInit prodAppInit = new ProdAppInit();
        prodAppInit.setEml(eml);
        prodAppInit.setProdAppType(prodAppType);
        prodAppInit.setSelSRA(selSRA);
        prodAppInit.setFee(fee);
        prodAppInit.setPrescreenfee(prescreenfee);
        prodAppInit.setTotalfee(totalfee);
        if (selSRA != null)
            prodAppInit.setSRA(selSRA.length > 0);
        else
            prodAppInit.setSRA(false);

        userSession.setProdAppInit(prodAppInit);
        prodAppInit.setMnVarQnt(minorQuantity);
        prodAppInit.setMjVarQnt(majorQuantity);
        return "/secure/prodreghome";
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
        eligible = false;
        if (userSession.isAdmin() || userSession.isHead() || userSession.isStaff())
            eligible = true;

        if (userSession.isCompany()) {
            if (userSession.getApplcantID() == null)
                eligible = false;
            else
                eligible = true;
        }
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
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
    
    @PostConstruct
    public void init() {
    	Long appId;
    		 appId = userSession.getApplcantID();
    		 if (appId==null){
   		prodAppTypes = new ArrayList<ProdAppType>();
    		prodAppTypes.add(ProdAppType.GENERIC);
    		prodAppTypes.add(ProdAppType.NEW_CHEMICAL_ENTITY);
    		prodAppTypes.add(ProdAppType.RENEW);
    		prodAppTypes.add(ProdAppType.VARIATION);
    		prodTable = null;
    	} else {
    		Long prodId = Scrooge.beanParam("prodID");
    		appId = Scrooge.beanParam("appID");
    		applicant=applicantService.findApplicant(appId);
    		 
    		if ((prodId==null)||(appId==null)){ //if something wrong, make it manually
    			Scrooge.setBeanParam("StandardProcedure", (long) 0);
    			init();
    		}
    		ProdApplications prodApp = prodApplicationsService.findProdApplications(appId);
    		//Product product = productDAO.findProduct(prodId);
    		//prodTable = createProdTableRecord(product);
    		
    	}
    	curUser = getUserSession().getUserService().findUser(userSession.getLoggedINUserID());
    }

	public List<ProdAppType> getProdAppTypes() {
		return prodAppTypes;
	}

	public void setProdAppTypes(List<ProdAppType> prodAppTypes) {
		this.prodAppTypes = prodAppTypes;
	}
	 public void ajaxListener(AjaxBehaviorEvent event){
	        isShowVariationType();
	         this.majorQuantity=0;
	        this.minorQuantity=0;
	        this.prodTable=null;
	           RequestContext.getCurrentInstance().update("reghome");
	    }

	public boolean isShowVariationType() {
		  showVariationType = (prodAppType==ProdAppType.VARIATION);
	        return showVariationType;
	}

	public void setShowVariationType(boolean showVariationType) {
		this.showVariationType = showVariationType;
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
        if (applicant == null){
            //prods = applicantService.findAllRegisteredProduct(applicant.getApplcntId());
        	prods = productService.findAllRegisteredProduct();
        	return prods;
        }
         List<ProdApplications> paList;
         Long a=applicant.getApplcntId();
         paList=applicantService.findRegProductForApplicant(a);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_YEAR,120);
        if (paList!=null){
           for (ProdApplications pa : paList) {
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
                       suggestions.add(createProdTableRecord(pa.getProduct()));
                   else {
                       Product p=pa.getProduct();
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

	public ApplicantService getApplicantService() {
		return applicantService;
	}

	public void setApplicantService(ApplicantService applicantService) {
		this.applicantService = applicantService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	 public void onItemSelect(SelectEvent event) {
	        if(event.getObject() instanceof ProdTable){
	            ProdTable prodTableCh = (ProdTable) event.getObject();
	            setProdTable(prodTableCh);
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", prodTable.getProdName()));
	        }  /* elseif(event.getObject() instanceof LicenseHolder){
	            LicenseHolder lc = (LicenseHolder) event.getObject();
	            setSelLicHolder(lc);
	        }*/
	    }
	 public ProdTable getProdTable() {
	        return prodTable;
	    }

	    public void setProdTable(ProdTable prodTable) {
	        this.prodTable = prodTable;
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
	    
}
