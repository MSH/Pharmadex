package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.dao.iface.InvoiceDAO;
import org.msh.pharmadex.domain.Invoice;
import org.msh.pharmadex.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Author: usrivastava
 */
@Service
public class InvoiceService implements Serializable {

    @Autowired
    InvoiceDAO invoiceDAO;

    @Autowired
    LetterService letterService;

    private Product product;
    private Invoice invoice;

    public String createInvoice(Invoice invoice, Product product) {
        this.invoice = invoice;
        this.product = product;

        try {
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_invoice", ".pdf");
            JasperPrint jasperPrint = initInvoice();
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            invoice.setInvoiceFile(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
            invoiceDAO.save(invoice);

        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "error";
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "error";
        }

        return "created";
    }

    public JasperPrint initInvoice() throws JRException {
//        Letter letter = letterService.findByLetterType(LetterType.INVOICE);
//        String body = letter.getBody();
//        MessageFormat mf = new MessageFormat(body);
//        Object[] args = {product.getProdName(), product.getApplicant().getAppName(), product.getProdApplications().getId()};
//        body = mf.format(args);

        URL resource = getClass().getResource("/reports/invoice.jasper");
        HashMap param = new HashMap();
        param.put("invoice_number", invoice.getInvoiceNumber());
        param.put("prod_name", product.getProdName());
        param.put("applicant_name", product.getApplicant().getAppName());
        param.put("expiry_date", product.getProdApplications().getRegExpiryDate());
        param.put("new_expiry_date", invoice.getNewExpDate());
        param.put("amount", invoice.getInvoiceAmt());
//        param.put("subject", "Subject: "+letter.getSubject()+" "+ product.getProdName() + " ");
//        param.put("body", body);
//        param.put("body", "Thank you for applying to register " + product.getProdName() + " manufactured by " + product.getApplicant().getAppName()
//                + ". Your application is successfully submitted and the application number is " + product.getProdApplications().getId() + ". "
//                +"Please use this application number for any future correspondence.");
        param.put("addr1", product.getApplicant().getAddress().getAddress1());
        param.put("addr2", product.getApplicant().getAddress().getAddress2());
        param.put("addr3", product.getApplicant().getAddress().getCountry().getCountryName());
        return JasperFillManager.fillReport(resource.getFile(), param);
    }


}
