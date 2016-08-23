package org.msh.pharmadex.mbean.lab;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.lab.SampleMed;
import org.msh.pharmadex.domain.lab.SampleStd;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class SampleRecBn implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SampleRecBn.class);

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    @ManagedProperty(value = "#{sampleTestService}")
    SampleTestService sampleTestService;

    @ManagedProperty(value = "#{userService}")
    UserService userService;

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;

    @ManagedProperty(value = "#{prodApplicationsService}")
    ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{productService}")
    ProductService productService;


    private SampleTest sampleTest;
    private SampleMed sampleMed;
    private SampleStd sampleStd;
    private ProdApplications prodApplications;

    private List<SampleMed> sampleMeds;
    private List<SampleStd> sampleStds;
    private User loggedInUser;
    private boolean displaySubmit;

    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

    @PostConstruct
    private void init() {
        try {
            if (sampleTest == null) {
                Long sampleTestID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sampleTestID"));
                if (sampleTestID != null) {
                    sampleTest = sampleTestService.findSampleTest(sampleTestID);
                    sampleMeds = sampleTest.getSampleMeds();
                    sampleStds = sampleTest.getSampleStds();
                    prodApplications = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
                }
                loggedInUser = userService.findUser(userSession.getLoggedINUserID());
            }
        } catch (Exception ex) {

        }
    }

    @Transactional
    public void addSampleMed() {
        facesContext = FacesContext.getCurrentInstance();

        if (sampleMeds == null)
            sampleMeds = new ArrayList<SampleMed>();

        sampleMeds.add(sampleMed);
        facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global.success")));
    }

    public String submitSample() {
        sampleTest.setUpdatedBy(loggedInUser);
        sampleTest.setUpdatedDate(new Date());
        saveSampleTest();
        return "/internal/lab/sampletestdetail";
    }


    public void initAddSampleMed() {
        Product product = null;
        if (prodApplications != null && prodApplications.getProduct() != null)
            product = productService.findProduct(prodApplications.getProduct().getId());
        sampleMed = new SampleMed(sampleTest, loggedInUser, new Country());
        sampleMed.setProduct(product);
    }

    public void removeMedSample(SampleMed sampleMed) {
        facesContext = FacesContext.getCurrentInstance();
        sampleMeds.remove(sampleMed);
        facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_delete")));

    }

    @Transactional
    public void addSampleStd() {
        facesContext = FacesContext.getCurrentInstance();

        if (sampleStds == null)
            sampleStds = new ArrayList<SampleStd>();

        sampleStds.add(sampleStd);
        facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global.success")));
    }


    public void initAddSampleStd() {
        sampleStd = new SampleStd(sampleTest, loggedInUser, new Country());
    }

    public void removeStdSample(SampleMed sampleMed) {
        facesContext = FacesContext.getCurrentInstance();
        sampleStds.remove(sampleStd);
        facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_delete")));

    }

    public void saveSampleTest() {
        facesContext = FacesContext.getCurrentInstance();
        RetObject retObject = sampleTestService.saveSample(sampleTest);
        if (!retObject.getMsg().equals("error")) {
            sampleTest = (SampleTest) retObject.getObj();
            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global.success")));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), null));

        }
    }

    public void cancelAddSampleMed() {
        sampleMed = null;
//        regHomeMbean.setShowCompany(false);
    }

    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = null;
        if (event.getObject() instanceof SampleMed) {
            sampleMed = (SampleMed) event.getObject();
            msg = new FacesMessage(sampleMed.getProduct().getProdName() + " updated");
        } else if (event.getObject() instanceof SampleStd) {
            sampleStd = (SampleStd) event.getObject();
            msg = new FacesMessage(sampleStd.getStdName() + " updated");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent event) {
//        FacesMessage msg = new FacesMessage(((ProdExcipient) event.getObject()).getExcipient().getName() + " updated");
//        FacesContext.getCurrentInstance().addMessage(null, msg);
    }


    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public SampleTestService getSampleTestService() {
        return sampleTestService;
    }

    public void setSampleTestService(SampleTestService sampleTestService) {
        this.sampleTestService = sampleTestService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public SampleTest getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(SampleTest sampleTest) {
        this.sampleTest = sampleTest;
    }

    public SampleMed getSampleMed() {
        return sampleMed;
    }

    public void setSampleMed(SampleMed sampleMed) {
        this.sampleMed = sampleMed;
    }

    public SampleStd getSampleStd() {
        return sampleStd;
    }

    public void setSampleStd(SampleStd sampleStd) {
        this.sampleStd = sampleStd;
    }

    public List<SampleMed> getSampleMeds() {
        return sampleMeds;
    }

    public void setSampleMeds(List<SampleMed> sampleMeds) {
        this.sampleMeds = sampleMeds;
    }

    public List<SampleStd> getSampleStds() {
        return sampleStds;
    }

    public void setSampleStds(List<SampleStd> sampleStds) {
        this.sampleStds = sampleStds;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public boolean isDisplaySubmit() {
//        if (userSession.isLab() || userSession.isStaff()) {
            if (sampleTest.getSampleTestStatus().ordinal() < 2)
                displaySubmit = true;
            else
                displaySubmit = false;
 //       } else {
 //           displaySubmit = false;
 //       }

        return displaySubmit;
    }

    public void setDisplaySubmit(boolean displaySubmit) {
        this.displaySubmit = displaySubmit;
    }
}
