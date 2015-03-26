package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timelineService;

    @ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{productService}")
    private ProductService productService;

    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;

    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;

    @ManagedProperty(value = "#{sampleTestService}")
    private SampleTestService sampleTestService;


    private FacesContext facesContext;
    private ResourceBundle resourceBundle;
    private TimeLine timeLine = new TimeLine();
    private boolean displayScreenAction;
    private User moderator;
    private SampleTest sampleTest;
    private UploadedFile file;

    private boolean attach;

    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);
    private JasperPrint jasperPrint;

    public String completeScreen() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        processProdBn.save();
        ProdApplications prodApplications = processProdBn.getProdApplications();
//        prodApplications.setProdAppChecklists(processProdBn.getProdAppChecklists());
        processProdBn.setModerator(moderator);
        processProdBn.assignModerator();

        if (!prodApplications.isPrescreenfeeReceived()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pre-screen fee not received", "Pre-screen fee not received"));
            return "";
        }
        if (prodApplications.getRegState().equals(RegState.NEW_APPL) || prodApplications.getRegState().equals(RegState.FOLLOW_UP)) {
            timeLine = new TimeLine();
            timeLine.setRegState(RegState.SCREENING);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(userSession.getLoggedInUserObj());
            timeLine.setComment("Pre-Screening completed successfully");
            processProdBn.setTimeLine(timeLine);
            RetObject retObject = timelineService.validatescreening(prodApplications.getProdAppChecklists());
            if (retObject.getMsg().equals("persist")) {
                addTimeline();
            } else {
                facesContext.addMessage(null, new FacesMessage("Please verify the dossier and update the checklist"));
            }

        }
        return null;
    }

    public String addTimeline() {
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

        try {

            ProdApplications prodApplications = processProdBn.getProdApplications();
            Product product = processProdBn.getProduct();
            timeLine.setProdApplications(prodApplications);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(userSession.getLoggedInUserObj());
            String retValue = timelineService.validateStatusChange(timeLine);

            if (retValue.equalsIgnoreCase("success")) {
                processProdBn.getTimeLineList().add(timeLine);
                prodApplications.setRegState(timeLine.getRegState());
                product.setRegState(timeLine.getRegState());
                prodApplications = prodApplicationsService.updateProdApp(prodApplications);
                processProdBn.setProdApplications(prodApplications);
                processProdBn.setFieldValues();
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
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void generateSampleRequestLetter() throws JRException, IOException {
        facesContext = FacesContext.getCurrentInstance();
        if (!processProdBn.getProdApplications().getRegState().equals(RegState.VERIFY)) {
            facesContext.addMessage(null, new FacesMessage("You can only issue a Sample Request letter after you have received the fee and verified the dossier for completeness"));
        }
        Product product = productService.findProduct(processProdBn.getProduct().getId());
        jasperPrint = reportService.generateSampleRequest(product, userSession.getLoggedInUserObj());
        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=sample_req_letter.pdf");
        httpServletResponse.setContentType("application/pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
//        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

        saveSample();
    }


    public void saveSample() {
        processProdBn.save();
        sampleTest.setLetterGenerated(true);
        sampleTest.setProdApplications(processProdBn.getProdApplications());
        sampleTest.setUser(userSession.getLoggedInUserObj());
        RetObject retObject = sampleTestService.saveSample(sampleTest);
        if(retObject.getMsg().equals("persist")) {
            sampleTest = (SampleTest) retObject.getObj();
        }else{
           FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error saving sample"));
        }
    }

    public void prescreenfeerecvd(){
        processProdBn.getProdApplications().setPrescreenfeeReceived(true);
    }

    public void changeStatusListener() {
        logger.error("Inside changeStatusListener");
        processProdBn.save();
        ProdApplications prodApplications = processProdBn.getProdApplications();
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
            timeLine.setRegState(RegState.FEE)  ;
            addTimeline();

        }

        processProdBn.setSelectedTab(2);
    }

    public StreamedContent fileDownload() {
        byte[] file1 = sampleTest.getFile();
        InputStream ist = new ByteArrayInputStream(file1);
        StreamedContent download = new DefaultStreamedContent(ist);
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
        return download;
    }

    public void handleFileUpload() {
        FacesMessage msg;
        facesContext = FacesContext.getCurrentInstance();
        resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

        if (file != null) {
            msg = new FacesMessage(resourceBundle.getString("global.success"), file.getFileName() + resourceBundle.getString("upload_success"));
            facesContext.addMessage(null, msg);
            try {
                sampleTest.setFile(IOUtils.toByteArray(file.getInputstream()));
                saveSample();
            } catch (IOException e) {
                msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
                FacesContext.getCurrentInstance().addMessage(null, msg);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            msg = new FacesMessage(resourceBundle.getString("upload_fail"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

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

    public void initTimeLine() {
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

    public TimelineService getTimelineService() {
        return timelineService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
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

    public TimeLine getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(TimeLine timeLine) {
        this.timeLine = timeLine;
    }

    public boolean isDisplayScreenAction() {
        if (processProdBn != null && processProdBn.getProdApplications() != null) {
            if (processProdBn.getProdApplications().getRegState().equals(RegState.NEW_APPL) || processProdBn.getProdApplications().getRegState().equals(RegState.FOLLOW_UP))
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

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public SampleTest getSampleTest() {
        if (sampleTest == null) {
            sampleTest = sampleTestService.findSampleForProd(processProdBn.getProdApplications().getId());
            if (sampleTest == null)
                sampleTest = new SampleTest();
        }
        return sampleTest;
    }

    public void setSampleTest(SampleTest sampleTest) {
        this.sampleTest = sampleTest;
    }

    public SampleTestService getSampleTestService() {
        return sampleTestService;
    }

    public void setSampleTestService(SampleTestService sampleTestService) {
        this.sampleTestService = sampleTestService;
    }

    public boolean isAttach() {
        if (getSampleTest().getFile() != null && getSampleTest().getFile().length > 0)
            return true;
        else
            return false;
    }

    public void setAttach(boolean attach) {
        this.attach = attach;
    }
}
