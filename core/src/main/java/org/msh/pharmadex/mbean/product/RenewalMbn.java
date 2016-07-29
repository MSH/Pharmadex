package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.AttachmentDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.PaymentStatus;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class RenewalMbn implements Serializable {

    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;
    @ManagedProperty(value = "#{userService}")
    UserService userService;
    @ManagedProperty(value = "#{checklistService}")
    ChecklistService checklistService;
    FacesContext context = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
    @ManagedProperty(value = "#{productService}")
    private ProductService productService;
    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{invoiceService}")
    private InvoiceService invoiceService;
    @ManagedProperty(value = "#{attachmentDAO}")
    private AttachmentDAO attachmentDAO;
    @ManagedProperty(value = "#{applicantService}")
    private ApplicantService applicantService;
    private ProdApplications prodApplications;
    private Product product;
    private Applicant applicant;
    private User applicantUser;
    private Invoice invoice;
    private Payment payment;
    private Reminder reminder;
    private List<Attachment> attachments;
    private Attachment attachment;
    private List<ForeignAppStatus> foreignAppStatuses;
    private List<ProdAppChecklist> prodAppChecklists;
    private UploadedFile file;
    private boolean showfull;

    @PostConstruct
    private void init() {
        try {
            Long prodID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("prodID"));
            if (prodID != null) {
                product = productService.findProduct(prodID);
                prodApplications = new ProdApplications(product);
                prodApplications.setProdAppType(ProdAppType.RENEW);
//                prodApplications.setSra(prodApp.isSRA());
//                prodApplications.setFastrack(prodApp.isEml());
//                prodApplications.setFeeAmt(prodApp.getFee());
//                prodApplications.setPrescreenfeeAmt(prodApp.getPrescreenfee());


                //being a new application. set regstate as saved
                prodApplications.setRegState(RegState.SAVED);


                //Set logged in user company as the company.
                if (userSession.isCompany()) {
                    applicantUser = userService.findUser(userSession.getLoggedINUserID());
                    prodApplications.setApplicant(applicantUser.getApplicant());
                    prodApplications.setApplicantUser(applicantUser);
                } else {
                    Long appID = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("appID"));
                    applicant = applicantService.findApplicant(appID);
                    applicantUser = (applicant.getUsers() != null && applicant.getUsers().size() > 0) ? applicant.getUsers().get(0) : null;
                    prodApplications.setApplicant(applicant);
                    prodApplications.setApplicantUser(applicantUser);

                }
                prodApplications.setCreatedBy(userService.findUser(userSession.getLoggedINUserID()));
            }
        }catch (Exception ex){

        }
    }

    public void addDocument() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        facesContext = FacesContext.getCurrentInstance();
//        file = userSession.getFile();
//        attachment.setFile(file.getContents());
        attachmentDAO.save(attachment);
        setAttachments(null);
//        userSession.setFile(null);
        FacesMessage msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
        facesContext.addMessage(null, msg);

    }


//    public void createInvoice() {
//        getSelProductApp();
//        invoice.setCurrExpDate(selProApp.getRegExpiryDate());
//        invoice.setInvoiceType(InvoiceType.RENEWAL);
//        invoice.setIssueDate(new Date());
//        invoice.setProdApplications(selProApp);
//        invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);
//
//
//        ArrayList<Invoice> invoices = (ArrayList<Invoice>) invoiceService.findInvoicesByProdApp(selProApp.getId());
//        if (invoices == null)
//            invoices = new ArrayList<Invoice>();
//        invoices.add(invoice);
////        selProApp.setInvoices(invoices);
//        processProdBn.setInvoices(invoices);
//
//        invoiceService.createInvoice(invoice, selProApp);
//
//
//    }


//    public void sendReminder() {
//        String result = null;
//        context = FacesContext.getCurrentInstance();
//        try {
//            result = invoiceService.sendReminder(getSelProductApp(), userSession.getLoggedINUserID(), invoice);
//            if (result.equalsIgnoreCase("reminder_sent")) {
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("reminder_sent")));
//            } else if (result.equalsIgnoreCase("no_invoice")) {
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("no_invoice"), bundle.getString("no_invoice")));
//            } else {
//
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("message")));
//        } catch (Exception ex) {
//            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
//        }
//    }

//    public void initInvoice() {
//        invoice = new Invoice();
//    }

    public void removeAppStatus(ForeignAppStatus foreignAppStatus) {
        context = FacesContext.getCurrentInstance();
        foreignAppStatuses.remove(foreignAppStatus);
        prodApplicationsService.removeForeignAppStatus(foreignAppStatus);
        context.addMessage(null, new FacesMessage(bundle.getString("company_removed")));
    }

    @Transactional
    public void saveApp() {
        context = FacesContext.getCurrentInstance();
        try {
//            prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
            Product product = productService.findProduct(prodApplications.getProduct().getId());
            prodApplications.setProduct(product);

            RetObject retObject = prodApplicationsService.updateProdApp(prodApplications, userSession.getLoggedINUserID());
            if(retObject.getMsg().equals("persist")) {
                prodApplications = (ProdApplications) retObject.getObj();
                context.addMessage(null, new FacesMessage(bundle.getString("app_save_success")));
            }else{
                context.addMessage(null, new FacesMessage(bundle.getString("save_app_error")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(bundle.getString("save_app_error")));
        }

    }

    //fires everytime you click on next or prev button on the wizard
    @Transactional
    public String onFlowProcess(FlowEvent event) {
        context = FacesContext.getCurrentInstance();
        String currentWizardStep = event.getOldStep();
        String nextWizardStep = event.getNewStep();
        try {
            initializeNewApp(nextWizardStep);
            if (!currentWizardStep.equals("prodreg"))
                saveApp();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(e.getMessage(), "Detail....");
            context.addMessage(null, msg);
            nextWizardStep = currentWizardStep; // keep wizard on current step if error
        }
        return nextWizardStep; // return new step if all ok
    }

    //Used to initialize field values only for new applications. For saved applications the values are assigned in setprodapplications
    @Transactional
    private void initializeNewApp(String currentWizardStep) {
        if (currentWizardStep.equals("prodreg") && product.getId() == null) {
        } else if (currentWizardStep.equals("proddetails")) {
            //Only initialize once for new product applications. For saved application it is initialized in the setprodapplication method
//            if (prodAppChecklists == null || prodAppChecklists.size() < 1) {
//                prodAppChecklists = new ArrayList<ProdAppChecklist>();
//                prodApplications.setProdAppChecklists(prodAppChecklists);
//                List<Checklist> allChecklist = checklistService.getChecklists(prodApplications.getProdAppType(), true);
//                ProdAppChecklist eachProdAppCheck;
//                if (allChecklist != null && allChecklist.size() > 0) {
//                    for (int i = 0; allChecklist.size() > i; i++) {
//                        eachProdAppCheck = new ProdAppChecklist();
//                        eachProdAppCheck.setChecklist(allChecklist.get(i));
//                        eachProdAppCheck.setProdApplications(prodApplications);
//                        prodAppChecklists.add(eachProdAppCheck);
//                    }
//                }
//                prodApplications.setProdAppChecklists(prodAppChecklists);

//            }
        } else if (currentWizardStep.equals("appdetails")) {

        } else if (currentWizardStep.equals("applicationStatus")) {

        } else if (currentWizardStep.equals("attach")) {
            if(prodAppChecklists!=null&&prodAppChecklists.size()>0)
                prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
        } else if (currentWizardStep.equals("prodAppChecklist")) {
            prodAppChecklists = prodApplicationsService.findAllProdChecklist(prodApplications.getId());
            if(prodAppChecklists!=null&&prodAppChecklists.size()<1) {
                prodAppChecklists = new ArrayList<ProdAppChecklist>();
                List<Checklist> allChecklist = checklistService.getChecklists(prodApplications, true);
                ProdAppChecklist eachProdAppCheck;
                if (allChecklist != null && allChecklist.size() > 0) {
                    for (int i = 0; allChecklist.size() > i; i++) {
                        eachProdAppCheck = new ProdAppChecklist();
                        eachProdAppCheck.setChecklist(allChecklist.get(i));
                        eachProdAppCheck.setProdApplications(prodApplications);
                        prodAppChecklists.add(eachProdAppCheck);
                    }
                }
            }
        } else if (currentWizardStep.equals("appointment")) {

        } else if (currentWizardStep.equals("summary")) {
            if (prodAppChecklists != null)
                prodApplicationsService.saveProdAppChecklists(prodAppChecklists);
        }

    }


    public void prepareUpload() {
        attachment = new Attachment();
        attachment.setUpdatedDate(new Date());
        attachment.setProdApplications(prodApplications);
        attachment.setRegState(RegState.SAVED);
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

        file = event.getFile();
        try {
            attachment.setFile(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        attachment.setProdApplications(prodApplications);
        attachment.setFileName(file.getFileName());
        attachment.setContentType(file.getContentType());
        attachment.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
        attachment.setRegState(prodApplications.getRegState());
//        attachmentDAO.save(attachment);
//        userSession.setFile(file);
    }


    public void deleteDoc(Attachment attach) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
        try {

            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_delete"), attach.getFileName() + resourceBundle.getString("is_deleted"));
            attachmentDAO.delete(attach);
            attachments = null;
            facesContext.addMessage(null, msg);
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), attach.getFileName() + resourceBundle.getString("cannot_delte"));
            facesContext.addMessage(null, msg);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Invoice getInvoice() {
        if (invoice == null)
            invoice = new Invoice();
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public void renew() {
        invoice.setPaymentStatus(PaymentStatus.PAYMENT_VERIFIED);
        invoice.setPayment(payment);
        invoiceService.renew(invoice, getProdApplications());

    }

    public String cancel() {
        userSession.setProdAppID(null);
        context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "renewalMbn", null);
        return "/public/registrationhome.faces";
    }

    private User getLoggedInUser(){
        return userService.findUser(userSession.getLoggedINUserID());
    }

    public String validateApp() {
        context = FacesContext.getCurrentInstance();
        prodApplications.setApplicant(applicant);
     // 22.07.2016
     			//prodApplications.setCreatedBy(applicantUser);
//        prodApplications.setForeignAppStatus(foreignAppStatuses);
        prodApplications.setProduct(product);
        if (product.getId() == null) {
            product.setCreatedBy(getLoggedInUser());
        }

        RetObject retObject = productService.validateProduct(prodApplications);

        if (retObject.getMsg().equals("persist")) {
            userSession.setProdAppID(prodApplications.getId());
            return "/secure/consentform.faces";
        } else {
            ArrayList<String> erroMsgs = (ArrayList<String>) retObject.getObj();
            for (String msg : erroMsgs) {
                context.addMessage(null, new FacesMessage(bundle.getString(msg)));
            }
            return "";
        }

    }

    public String reportPayment() {
        payment.setPaymentDate(new Date());
        invoice.setPaymentStatus(PaymentStatus.PAID);
        payment.setInvoice(invoice);
        invoice.setPayment(payment);
        String result = invoiceService.savePayment(payment);

        return result;  //To change body of created methods use File | Settings | File Templates.
    }

    public Payment getPayment() {
        if (payment == null)
            payment = new Payment();
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String preparePayment() {
        if (getProdApplications() != null) {
            List<Invoice> invoices = invoiceService.findInvoicesByProdApp(getProdApplications().getId());
            for (Invoice i : invoices) {
                setInvoice(i);
                setPayment(i.getPayment());
            }
        }
        return "";
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public InvoiceService getInvoiceService() {
        return invoiceService;
    }

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<ForeignAppStatus> getForeignAppStatuses() {
//        if (foreignAppStatuses == null) {
//            foreignAppStatuses = prodApplicationsService.findForeignAppStatus(prodApplications.getId());
//            if (foreignAppStatuses == null)
//                foreignAppStatuses = new ArrayList<ForeignAppStatus>();
//        }
        return foreignAppStatuses;
    }

    public void setForeignAppStatuses(List<ForeignAppStatus> foreignAppStatuses) {
        this.foreignAppStatuses = foreignAppStatuses;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachments() {
        if (attachments == null&&getProdApplications()!=null&&getProdApplications().getId()!=null)
            attachments = (ArrayList<Attachment>) attachmentDAO.findByProdApplications_Id(getProdApplications().getId());
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public AttachmentDAO getAttachmentDAO() {
        return attachmentDAO;
    }

    public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
        this.attachmentDAO = attachmentDAO;
    }

    public List<ProdAppChecklist> getProdAppChecklists() {
        return prodAppChecklists;
    }

    public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
        this.prodAppChecklists = prodAppChecklists;
    }

    public ApplicantService getApplicantService() {
        return applicantService;
    }

    public void setApplicantService(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public ChecklistService getChecklistService() {
        return checklistService;
    }

    public void setChecklistService(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    public User getApplicantUser() {
        return applicantUser;
    }

    public void setApplicantUser(User applicantUser) {
        this.applicantUser = applicantUser;
    }

    public boolean isShowfull() {
        if (userSession.isStaff() || userSession.isModerator() || userSession.isReviewer() || userSession.isLab() || userSession.isClinical()) {
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
}
