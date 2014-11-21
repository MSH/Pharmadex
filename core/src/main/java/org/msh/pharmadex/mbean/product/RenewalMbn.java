package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.InvoiceType;
import org.msh.pharmadex.domain.enums.PaymentStatus;
import org.msh.pharmadex.service.InvoiceService;
import org.msh.pharmadex.service.ProductService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class RenewalMbn implements Serializable {

    @ManagedProperty(value = "#{processProdBn}")
    ProcessProdBn processProdBn;
    @ManagedProperty(value = "#{userSession}")
    UserSession userSession;
    FacesContext context = FacesContext.getCurrentInstance();
    java.util.ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msgs");
    @ManagedProperty(value = "#{productService}")
    private ProductService productService;
    @ManagedProperty(value = "#{invoiceService}")
    private InvoiceService invoiceService;
    private ProdApplications selProApp;
    private Product selProd;
    private Invoice invoice;
    private Payment payment;
    private Reminder reminder;

    public Product getSelProd() {
        Product p = selProApp.getProd();
        p.setProdApplications(selProApp);
        return selProApp.getProd();
    }

    public void setSelProd(Product selProd) {
        this.selProd = selProd;
    }

    @PostConstruct
    public void init() {
        preparePayment();
    }

    public void createInvoice() {
        getSelProductApp();
        invoice.setCurrExpDate(selProApp.getRegExpiryDate());
        invoice.setInvoiceType(InvoiceType.RENEWAL);
        invoice.setIssueDate(new Date());
        invoice.setProdApplications(selProApp);
        invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);


        ArrayList<Invoice> invoices = (ArrayList<Invoice>) invoiceService.findInvoicesByProdApp(selProApp.getId());
        if (invoices == null)
            invoices = new ArrayList<Invoice>();
        invoices.add(invoice);
        selProApp.setInvoices(invoices);
        processProdBn.setInvoices(invoices);

        invoiceService.createInvoice(invoice, selProApp);


    }


    public void sendReminder() {
        String result = null;
        context = FacesContext.getCurrentInstance();
        try {
            result = invoiceService.sendReminder(getSelProductApp(), userSession.getLoggedInUserObj(), invoice);
            if (result.equalsIgnoreCase("reminder_sent")) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("global.success"), bundle.getString("reminder_sent")));
            } else if (result.equalsIgnoreCase("no_invoice")) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getString("no_invoice"), bundle.getString("no_invoice")));
            } else {

            }
        } catch (MessagingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("message")));
        } catch (Exception ex) {
            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("global_fail"), bundle.getString("global_fail")));
        }
    }

    public void initInvoice() {
        invoice = new Invoice();
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

    public ProdApplications getSelProductApp() {
        if (selProApp == null) {
            selProApp = processProdBn.getProdApplications();
        }
        return selProApp;
    }

    public void setSelProApp(ProdApplications selProApp) {
        this.selProApp = selProApp;
    }

    public void renew() {
        invoice.setPaymentStatus(PaymentStatus.PAYMENT_VERIFIED);
        invoice.setPayment(payment);
        invoiceService.renew(invoice, getSelProductApp());

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

    public String preparePayment() {
        List<Invoice> invoices = invoiceService.findInvoicesByProdApp(getSelProductApp().getId());
        for (Invoice i : invoices) {
            setInvoice(i);
            setPayment(i.getPayment());
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

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
