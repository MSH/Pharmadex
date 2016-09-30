/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.hibernate.Hibernate;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.dao.iface.ProdAppLetterDAO;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.SuspDetail;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.SuspensionStatus;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.service.SuspendServiceMZ;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RetObject;
import org.msh.pharmadex.util.Scrooge;

/**
*/
@ManagedBean
@ViewScoped
public class SuspendDetailBnMZ implements Serializable {

    @ManagedProperty(value = "#{globalEntityLists}")
    private GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{suspendServiceMZ}")
    private SuspendServiceMZ suspendServiceMZ;

    @ManagedProperty(value = "#{reviewService}")
    private ReviewService reviewService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{prodAppLetterDAO}")
    private ProdAppLetterDAO prodAppLetterDAO;

    @ManagedProperty(value = "#{attachmentDAO}")
    private AttachmentDAO attachmentDAO;

    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;

    private SuspDetail suspDetail;
    private Product product;
    private ProdApplications prodApplications;
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
    private User loggedInUser;
    
    private String sourcePage = "/public/registrationhome.faces";
    private boolean editForm = true;

    @PostConstruct
    private void init() {
        try {
            facesContext = FacesContext.getCurrentInstance();
            Long suspID=Scrooge.beanParam("suspDetailID");
            if (suspID == null) {// this is new suspension record
            	editForm = true;
                Long prodAppID = Scrooge.beanParam("prodAppID");
                if (prodAppID != null) {
                    prodApplications = prodApplicationsService.findProdApplications(prodAppID);
                    product = prodApplications.getProduct();
                    suspDetail = new SuspDetail(prodApplications);
                    suspDetail.setDecision(RecomendType.SUSPEND);
                    suspDetail.setSuspensionStatus(SuspensionStatus.REQUESTED);
                    suspDetail.setCreatedBy(getLoggedInUser());
                    suspDetail.setCreatedDate(Calendar.getInstance().getTime());
                }
            }else{
                 getLoggedInUser();
                 suspDetail = suspendServiceMZ.findSuspendDetail(suspID);
                 prodApplications = prodApplicationsService.findProdApplications(suspDetail.getProdApplications().getId());
                 if (prodApplications.getProduct() != null)
                     Hibernate.initialize(prodApplications.getProduct());
                 product = prodApplications.getProduct();
                 if (prodApplications.getApplicant() != null)
                    Hibernate.initialize(prodApplications.getApplicant());

                editForm = false;
            }
            String str = Scrooge.beanStrParam("sourcePage");
            if(str != null && !str.equals(""))
            	sourcePage = str;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private User getLoggedInUser() {
        if (loggedInUser == null) {
            loggedInUser = userService.findUser(userSession.getLoggedINUserID());
        }
        return loggedInUser;
    }

    /**
     * Main suspend procedure, action depends of document status and user position
     * @return
     */
    public String submitSuspend() {
        facesContext = FacesContext.getCurrentInstance();
        if (userSession.isHead()) {//head user (director)
            RetObject retObject = suspendServiceMZ.submitHead(suspDetail, userSession.getLoggedINUserID(), RegState.SUSPEND);
            if (retObject.getMsg().equals("error")) {
                FacesMessage fm = new FacesMessage("Error");
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, fm);
                return "";
            }
        }

        return "/internal/processcancellist.faces" + "?faces-redirect=true";
    }

    public List<RegState> getDecisionType() {
        List<RegState> decisionType = new ArrayList<RegState>();
        decisionType.add(RegState.SUSPEND);
        decisionType.add(RegState.CANCEL);
        decisionType.add(RegState.REGISTERED);
        return decisionType;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
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

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public SuspendServiceMZ getSuspendServiceMZ() {
        return suspendServiceMZ;
    }

    public void setSuspendServiceMZ(SuspendServiceMZ suspendService) {
        this.suspendServiceMZ = suspendService;
    }

    public SuspDetail getSuspDetail() {
        return suspDetail;
    }

    public void setSuspDetail(SuspDetail suspDetail) {
        this.suspDetail = suspDetail;
    }

    public ProdAppLetterDAO getProdAppLetterDAO() {
        return prodAppLetterDAO;
    }

    public void setProdAppLetterDAO(ProdAppLetterDAO prodAppLetterDAO) {
        this.prodAppLetterDAO = prodAppLetterDAO;
    }

    public TimelineService getTimelineService() {
        return timelineService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public AttachmentDAO getAttachmentDAO() {
        return attachmentDAO;
    }

    public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
        this.attachmentDAO = attachmentDAO;
    }

    public String showProductDetails() {
        String res = "";
        if (suspDetail.getId() == null) {
            res = submitSuspend();
            if ("".equals(res)) return "";
//        facesContext.getExternalContext().getFlash().put("prodAppID", String.valueOf(suspDetail.getProdApplications().getId()));
            facesContext.getExternalContext().getFlash().put("suspDetailID", String.valueOf(suspDetail.getId()));
        }
        return "/internal/processreg";
    }

	public String getSourcePage() {
		return sourcePage;
	}

	public void setSourcePage(String sourcePage) {
		this.sourcePage = sourcePage;
	}

	public boolean isEditForm() {
		return editForm;
	}

	public void setEditForm(boolean editForm) {
		this.editForm = editForm;
	}
    
    
}
