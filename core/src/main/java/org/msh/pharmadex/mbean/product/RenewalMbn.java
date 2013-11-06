package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Invoice;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.Reminder;
import org.msh.pharmadex.domain.enums.InvoiceType;
import org.msh.pharmadex.failure.UserSession;
import org.msh.pharmadex.service.InvoiceService;
import org.msh.pharmadex.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class RenewalMbn implements Serializable {

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    ProcessProdBn processProdBn;

    @Autowired
    UserSession userSession;

    private Product selProduct;

    private Invoice invoice = new Invoice();

    private Reminder reminder;

    public void createInvoice() {
        getSelProduct();
        invoice.setCurrExpDate(selProduct.getProdApplications().getRegExpiryDate());
        invoice.setInvoiceType(InvoiceType.RENEWAL);
        invoice.setIssueDate(new Date());
        invoice.setProdApplications(selProduct.getProdApplications());


        ArrayList<Invoice> invoices = (ArrayList<Invoice>) invoiceService.findInvoicesByProdApp(selProduct.getProdApplications().getId());
        if (invoices == null)
            invoices = new ArrayList<Invoice>();
        invoices.add(invoice);
        selProduct.getProdApplications().setInvoices(invoices);
        processProdBn.setInvoices(invoices);

        invoiceService.createInvoice(invoice, selProduct);


    }

    public void sendReminder() {
        String result = null;
        try {
            result = invoiceService.sendReminder(getSelProduct(), userSession.getLoggedInUserObj(), invoice);
            if (result.equalsIgnoreCase("reminder_sent")) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Reminder sent"));
            } else if (result.equalsIgnoreCase("no_invoice")) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No Invoice", "No invoice present"));
            } else {

            }
        } catch (MessagingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during sending the reminder"));
        } catch (Exception ex) {
            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error during the process"));
        }
    }

    public void initInvoice() {
        invoice = new Invoice();
    }


    public Invoice getInvoice() {
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

    public Product getSelProduct() {
        if (selProduct == null) {
            selProduct = processProdBn.getProdApplications().getProd();
        }
        return selProduct;
    }

    public void setSelProduct(Product selProduct) {
        this.selProduct = selProduct;
    }
}
