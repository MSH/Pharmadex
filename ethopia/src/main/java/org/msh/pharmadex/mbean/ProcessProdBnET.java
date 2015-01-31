package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.TimelineServiceET;
import org.msh.pharmadex.util.RetObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@SessionScoped
public class ProcessProdBnET implements Serializable {

    @ManagedProperty(value = "#{timelineServiceET}")
    private TimelineServiceET timelineServiceET;

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private FacesContext facesContext;
    private ResourceBundle resourceBundle;
    private ProdApplications prodApplications;
    private TimeLine timeLine = new TimeLine();
    private boolean displayScreenAction;
    private User moderator;

    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);

    public String completeScreen() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        prodApplications = processProdBn.getProdApplications();
        processProdBn.setModerator(moderator);
        processProdBn.assignModerator();
        if(!prodApplications.isPrescreenfeeReceived()){
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pre-screen fee not received","Pre-screen fee not received"));
            return "";
        }
        if (prodApplications.getRegState().equals(RegState.NEW_APPL)||prodApplications.getRegState().equals(RegState.FOLLOW_UP)) {
            timeLine = new TimeLine();
            timeLine.setRegState(RegState.SCREENING);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(userSession.getLoggedInUserObj());
            timeLine.setComment("Pre-Screening completed successfully");
            processProdBn.setTimeLine(timeLine);
            RetObject retObject = timelineServiceET.validatescreening(prodApplications.getProdAppChecklists());
            if (retObject.getMsg().equals("persist")) {
                addTimeline();
            } else {
                facesContext.addMessage(null, new FacesMessage("Please verify the dossier and update the checklist"));
            }

        }
        return "";
    }

    public String addTimeline() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

        try {

            prodApplications = processProdBn.getProdApplications();
            Product product = processProdBn.getProduct();
            timeLine.setProdApplications(prodApplications);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(userSession.getLoggedInUserObj());
            String retValue = timelineServiceET.validateStatusChange(timeLine);

            if (retValue.equalsIgnoreCase("success")) {
                processProdBn.getTimeLineList().add(timeLine);
                prodApplications.setRegState(timeLine.getRegState());
                product.setRegState(timeLine.getRegState());
                prodApplications = prodApplicationsService.updateProdApp(prodApplications);
                product = productService.findProduct(prodApplications.getProd().getId());
                processProdBn.setProduct(product);
                processProdBn.setProdApplications(prodApplications);
                processProdBn.setFieldValues();
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
            } else if (retValue.equalsIgnoreCase("fee_not_recieved")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            } else if (retValue.equalsIgnoreCase("app_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            } else if (retValue.equalsIgnoreCase("prod_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), resourceBundle.getString("prod_not_verified")));
            } else if (retValue.equalsIgnoreCase("valid_assign_moderator")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_moderator")));
            } else if (retValue.equalsIgnoreCase("valid_assign_reviewer")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_reviewer")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
        }
        timeLine = new TimeLine();
        return "";  //To change body of created methods use File | Settings | File Templates.
    }

    public void changeStatusListener() {
        logger.error("Inside changeStatusListener");
        prodApplications = processProdBn.getProdApplications();
        timeLine = new TimeLine();

        if (prodApplications.getRegState().equals(RegState.NEW_APPL)) {
            timeLine.setRegState(RegState.FEE);
            addTimeline();
        }
        if (prodApplications.getRegState().equals(RegState.FEE)) {
            if (prodApplications.isApplicantVerified() && prodApplications.isProductVerified() && prodApplications.isDossierReceived()) {
                timeLine.setRegState(RegState.VERIFY);
                addTimeline();
            }
        }
        if (prodApplications.getRegState().equals(RegState.SCREENING)) {
                timeLine.setRegState(RegState.FEE);
                addTimeline();

        }

        processProdBn.setSelectedTab(2);
    }


    public String sendToApplicant() {
//        TimeLine timeLine = getTimeLine();
//        timeLine = new TimeLine();
        timeLine.setRegState(RegState.FOLLOW_UP);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        processProdBn.setTimeLine(timeLine);
        addTimeline();
        return "";
    }

    public void initTimeLine(){
        timeLine = new TimeLine();

    }

    public String archiveApp() {
//        TimeLine timeLine = getTimeLine();
//        timeLine = new TimeLine();
        timeLine.setRegState(RegState.DEFAULTED);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(userSession.getLoggedInUserObj());
        processProdBn.setTimeLine(timeLine);
        addTimeline();
        return "";

    }

    public TimelineServiceET getTimelineServiceET() {
        return timelineServiceET;
    }

    public void setTimelineServiceET(TimelineServiceET timelineServiceET) {
        this.timelineServiceET = timelineServiceET;
    }

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public TimeLine getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(TimeLine timeLine) {
        this.timeLine = timeLine;
    }

    public boolean isDisplayScreenAction() {
        if(processProdBn!=null&&processProdBn.getProdApplications()!=null){
            if(processProdBn.getProdApplications().getRegState().equals(RegState.NEW_APPL)||processProdBn.getProdApplications().getRegState().equals(RegState.FOLLOW_UP))
                displayScreenAction = true;
            else
                displayScreenAction = false;
        }
        return displayScreenAction;
    }

    public void setDisplayScreenAction(boolean displayScreenAction) {
        this.displayScreenAction = displayScreenAction;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }


}
