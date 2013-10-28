package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Invoice;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.Reminder;
import org.msh.pharmadex.domain.enums.InvoiceType;
import org.msh.pharmadex.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: usrivastava
 */
@Component
@Scope("request")
public class RenewalMbn implements Serializable {

    @Autowired
    private ProductService productService;

    private Product selProduct;

    private Invoice invoice;

    private Reminder reminder;

    public void createInvoice() {
        invoice.setCurrExpDate(selProduct.getProdApplications().getRegExpiryDate());
        invoice.setInvoiceType(InvoiceType.RENEWAL);
        invoice.setIssueDate(new Date());
        invoice.setProdApplications(selProduct.getProdApplications());

        List<Invoice> invoices = selProduct.getProdApplications().getInvoices();
        if (invoices == null)
            invoices = new ArrayList<Invoice>();
        invoices.add(invoice);
        selProduct.getProdApplications().setInvoices(invoices);


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
        return selProduct;
    }

    public void setSelProduct(Product selProduct) {
        this.selProduct = selProduct;
    }
}
