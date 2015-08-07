package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
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
    private Product product;
    private Applicant applicant;
    private ProdApplications prodApplications;
    private List<Timeline> timelinesChartData;
    private List<ProdAppChecklist> prodAppChecklists;
    private List<ForeignAppStatus> foreignAppStatuses;


    @PostConstruct
    private void init() {
        Long prodAppID = (Long) JsfUtils.flashScope().get("prodAppID");
        if (prodAppID != null) {
            prodApplications = prodApplicationsService.findProdApplications(prodAppID);
            product = productService.findProduct(prodApplications.getProduct().getId());
            prodApplications.setProduct(product);
            applicant = prodApplications.getApplicant();
            timeLineList = timelineService.findTimelineByApp(prodApplications.getId());
            prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
            foreignAppStatuses = prodApplicationsService.findForeignAppStatus(prodApplications.getId());
            FacesContext.getCurrentInstance().getExternalContext().getFlash().keep("prodAppID");
        }
    }

    public String sentToDetail() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("appID", applicant.getApplcntId());
        return "applicantdetail";
    }

    public String sentToApp() {
//        userSession.setReview(null);
        JsfUtils.flashScope().put("prodAppID", prodApplications.getId());
        return "/internal/processreg";

    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public TimelineModel getTimelinesChartData() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
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
        Long prodAppID = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("prodAppID");
        if(prodAppID!=null) {
            prodApplications = prodApplicationsService.findProdApplicationByProduct(prodAppID);
            product = prodApplications.getProduct();
            applicant = prodApplications.getApplicant();
            FacesContext.getCurrentInstance().getExternalContext().getFlash().keep("prodAppID");
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
}
