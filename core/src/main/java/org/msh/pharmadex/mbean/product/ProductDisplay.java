package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.mbean.BackLog;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.util.Scrooge;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static javax.faces.context.FacesContext.*;

/**
 * Author: usrivastava, updated by Odissey
 */
@ManagedBean
@ViewScoped
public class ProductDisplay implements Serializable {

    protected List<TimeLine> timeLineList;
    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{productService}")
    ProductService productService;
    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    private Product product;
    private Applicant applicant;
    private ProdApplications prodApplications;
    private List<Timeline> timelinesChartData;
    private List<ProdAppChecklist> prodAppChecklists;
    private List<ForeignAppStatus> foreignAppStatuses;
    private boolean showfull = false;

    @PostConstruct
    private void init() {
        try {
            Long prodAppID = Scrooge.beanParam("prodAppID");
            /*
            if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("prodAppID"))
                prodAppID = Long.valueOf(Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("prodAppID")));
            else{
                prodAppID = (Long) getCurrentInstance().getExternalContext().getFlash().get("prodAppID");
            }
            */

            if (prodAppID != null) {
                prodApplications = prodApplicationsService.findProdApplications(prodAppID);
                product = productService.findProduct(prodApplications.getProduct().getId());
                prodApplications.setProduct(product);
                applicant = prodApplications.getApplicant();
                timeLineList = timelineService.findTimelineByApp(prodApplications.getId());
                prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
                foreignAppStatuses = prodApplicationsService.findForeignAppStatus(prodApplications.getId());
                getCurrentInstance().getExternalContext().getFlash().keep("prodAppID");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String sentToDetail() {
        Flash flash = getCurrentInstance().getExternalContext().getFlash();
        flash.put("appID", applicant.getApplcntId());
        return "applicantdetail";
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public TimelineModel getTimelinesChartData() {
        FacesContext facesContext = getCurrentInstance();
        java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        getProdApplications();
        timelinesChartData = new ArrayList<Timeline>();
        Timeline timeline;
        TimelineModel model = new TimelineModel();
        if (timeLineList != null) {
            for (org.msh.pharmadex.domain.TimeLine tm : getTimeLineList()) {
                timeline = new Timeline();
                model.add(new TimelineEvent(resourceBundle.getString(tm.getRegState().getKey()), tm.getStatusDate()));
                timelinesChartData.add(timeline);
            }
        }
        return model;
    }

    public void setTimelinesChartData(List<Timeline> timelinesChartData) {
        this.timelinesChartData = timelinesChartData;
    }

    private void initFields() {
        Long prodAppID = (Long) getCurrentInstance().getExternalContext().getFlash().get("prodAppID");
        if (prodAppID != null) {
            List<ProdApplications> prodApps = prodApplicationsService.findProdApplicationByProduct(product.getId());
            prodApplications = (prodApps != null && prodApps.size() > 0) ? prodApps.get(0) : null;
            product = prodApplications.getProduct();
            applicant = prodApplications.getApplicant();
            getCurrentInstance().getExternalContext().getFlash().keep("prodAppID");
        }
    }

    //if certificate didn't generate on previous stage, do it
    public StreamedContent generateCertificate(){
        String result = "";
        if (prodApplications.getRegCert()==null)
            result = prodApplicationsService.createRegCert(prodApplications);
        else
            result = "created";
        if (!"created".equals(result)){
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Certificate didn't create"));
            return null;
        }else{
            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Success","Just open certificate by 'Certificate' button"));
            InputStream ist = new ByteArrayInputStream(prodApplications.getRegCert());
            Calendar c = Calendar.getInstance();
            StreamedContent download = new DefaultStreamedContent(ist, "pdf", "registration_" + prodApplications.getId() + "_" + c.get(Calendar.YEAR)+".pdf");
            return download;
        }
    }

    public ProdApplications getProdApplications() {
        if (prodApplications == null)
            initFields();
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
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

    public TimelineService getTimelineService() {
        return timelineService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public List<TimeLine> getTimeLineList() {
        return timeLineList;
    }

    public void setTimeLineList(List<TimeLine> timeLineList) {
        this.timeLineList = timeLineList;
    }

    public List<ProdAppChecklist> getProdAppChecklists() {
        return prodAppChecklists;
    }

    public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        this.prodAppChecklists = prodAppChecklists;
    }

    public List<ForeignAppStatus> getForeignAppStatuses() {
        return foreignAppStatuses;
    }

    public void setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatuses) {
        this.foreignAppStatuses = foreignAppStatuses;
    }

    public boolean isShowfull() {
        if (userSession.isStaff() || userSession.isModerator() || userSession.isReviewer() || userSession.isLab() || userSession.isClinical()|| userSession.isInspector()) {
            showfull = true;
        } else if (userSession.isCompany()) {
            if (prodApplications.getApplicant().getApplcntId().equals(userSession.getApplcantID())) {
                showfull = true;
            } else {
                showfull = false;
            }
        }
        return showfull;
    }


    public void setShowfull(boolean showfull) {
        this.showfull = showfull;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
    
    public String goToBack(){
    	if(userSession.getLoggedINUserID() != null && userSession.getLoggedINUserID() > 0)
    	{
            if(userSession.isReviewer()){
    			return "/public/productlist.faces";
    		}
    		return "/internal/processprodlist.faces";
    	}
        String srcPage = Scrooge.beanStrParam("SourcePage");
        if (srcPage==null)
    	    return "/public/productlist.faces";
        else
            return parseSourcePage(srcPage);
    }

    public String parseSourcePage(String sourcePage){
        String[] src = sourcePage.split(":");
        if (src.length==1) return sourcePage; //return to list
        String id=src[0];
        String page = src[1];
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if (page.contains("licenseholderlist")){
            Scrooge.setBeanParam("licHolderID",Long.parseLong(id));
            return page;
        }else if (page.contains("process")){
            Scrooge.setBeanParam("appID",Long.parseLong(id));
            return page;
        }
        return "";
    }

}
