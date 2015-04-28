package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.UserDAO;
import org.msh.pharmadex.dao.iface.InvoiceDAO;
import org.msh.pharmadex.dao.iface.PaymentDAO;
import org.msh.pharmadex.dao.iface.ReminderDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.InvoiceType;
import org.msh.pharmadex.domain.enums.PaymentStatus;
import org.msh.pharmadex.domain.enums.ReminderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class InvoiceService implements Serializable {

    @Autowired
    InvoiceDAO invoiceDAO;

    @Autowired
    PaymentDAO paymentDAO;

    @Autowired
    ReminderDAO reminderDAO;

    @Autowired
    LetterService letterService;

    @Autowired
    MailService mailService;

    @Autowired
    UserDAO userDAO;

    @Autowired
    ProdApplicationsDAO prodApplicationsDAO;

    private Product product;
    private Invoice invoice;
    private ProdApplications prodApplications;

    public List<Invoice> findInvoicesByProdApp(Long id) {
        return invoiceDAO.findByProdApplications_Id(id);
    }

    public String createInvoice(Invoice invoice, ProdApplications prodApp) {
        this.invoice = invoice;
        this.prodApplications = prodApp;
        this.product = prodApp.getProduct();

        try {
//            invoice.setPaymentStatus(PaymentStatus.INVOICE_ISSUED);
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_invoice", ".pdf");
            JasperPrint jasperPrint = initInvoice();
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            invoice.setInvoiceFile(IOUtils.toByteArray(new FileInputStream(invoicePDF)));
            invoiceDAO.saveAndFlush(invoice);

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
//        Object[] args = {prodApp.getProdName(), prodApp.getApplicant().getAppName(), prodApp.getProdApplications().getId()};
//        body = mf.format(args);

        prodApplications = prodApplicationsDAO.findProdApplicationByProduct(product.getId());
        String regExpDt = DateFormat.getDateInstance().format(invoice.getNewExpDate());
        String expDt = DateFormat.getDateInstance().format(prodApplications.getRegExpiryDate());

        URL resource = getClass().getResource("/reports/invoice.jasper");
        HashMap param = new HashMap();
        param.put("invoice_number", invoice.getInvoiceNumber());
        param.put("prod_name", product.getProdName());
        param.put("applicant_name", prodApplications.getApplicant().getAppName());
        param.put("expiry_date", expDt);
        param.put("new_expiry_date", regExpDt);
        param.put("amount", invoice.getInvoiceAmt());
//        param.put("subject", "Subject: "+letter.getSubject()+" "+ prodApp.getProdName() + " ");
//        param.put("body", body);
//        param.put("body", "Thank you for applying to register " + prodApp.getProdName() + " manufactured by " + prodApp.getApplicant().getAppName()
//                + ". Your application is successfully submitted and the application number is " + prodApp.getProdApplications().getId() + ". "
//                +"Please use this application number for any future correspondence.");
        param.put("addr1", prodApplications.getApplicant().getAddress().getAddress1());
        param.put("addr2", prodApplications.getApplicant().getAddress().getAddress2());
        param.put("addr3", prodApplications.getApplicant().getAddress().getCountry().getCountryName());
        return JasperFillManager.fillReport(resource.getFile(), param);
    }


    public String sendReminder(ProdApplications selProdApp, Long userID, Invoice invoice) throws MessagingException {
        Reminder reminder = new Reminder();

        if (invoice.getInvoiceType().equals(InvoiceType.RENEWAL)) {
            List<Reminder> reminders = reminderDAO.findByInvoice_Id(invoice.getId());
            switch (reminders.size()) {
                case 1:
                    reminder.setReminderType(ReminderType.FIRST);
                    break;
                case 2:
                    reminder.setReminderType(ReminderType.SECOND);
                    break;
                case 3:
                    reminder.setReminderType(ReminderType.THIRD);
                    break;
                case 4:
                    reminder.setReminderType(ReminderType.DE_REGISTER);
                    break;
                default:
                    reminder.setReminderType(ReminderType.INVOICE);
            }
            reminder.setInvoice(invoice);
            reminder.setSentDate(new Date());
            reminder.setReminderType(ReminderType.INVOICE);
            reminders.add(reminder);
            invoice.setReminders(reminders);
        }


        Mail mail = new Mail();
        mail.setMailto(selProdApp.getApplicant().getEmail());
        mail.setProdApplications(selProdApp);
        mail.setSubject("Pharmadex Registration Renewal");
        mail.setUser(userDAO.findUser(userID));
        mail.setDate(new Date());
        mail.setMessage("Please renew your registration. The registration invoice is attached. If you have trouble opening the attachment you can also check it out by logging into your account.");

        mailService.sendMailWithAttach(mail, true, reminder.getInvoice().getInvoiceFile());
        reminderDAO.saveAndFlush(reminder);
        invoiceDAO.saveAndFlush(invoice);
        return "reminder_sent";

    }

    public List<ProdApplications> findPendingByApplicant(Long applicantID) {
        if (applicantID != null) {
            List<User> users = userDAO.findByApplicant(applicantID);
            //        List<Invoice> pendInvoices = invoiceDAO.findByProdApplications_ProdApplicant_UsersAndPaymentStatus(users, PaymentStatus.INVOICE_ISSUED);
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("users", users);
            params.put("paymentStatus", PaymentStatus.INVOICE_ISSUED);

            return prodApplicationsDAO.findPendingRenew(params);  //To change body of created methods use File | Settings | File Templates.
        } else
            return null;
    }

    public String savePayment(Payment payment) {
        invoiceDAO.saveAndFlush(payment.getInvoice());
//        paymentDAO.save(payment);
        return "persisted";  //To change body of created methods use File | Settings | File Templates.
    }

    public String renew(Invoice invoice, ProdApplications selProduct) {
        invoiceDAO.saveAndFlush(invoice);
        ProdApplications prodApplications = prodApplicationsDAO.findProdApplications(selProduct.getId());
        prodApplications.setRegExpiryDate(invoice.getNewExpDate());
        prodApplicationsDAO.saveApplication(prodApplications);
        return "persisted";
    }
}
