package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportService implements Serializable {

    @Autowired
    LetterService letterService;

    @Autowired
    ProductService productService;



    public JasperPrint reportinit(Product product) throws JRException {
//        Letter letter = letterService.findByLetterType(LetterType.ACK_SUBMITTED);
//        String body = letter.getBody();
//        MessageFormat mf = new MessageFormat(body);
//        Object[] args = {product.getProdName(), product.getApplicant().getAppName(), product.getProdApplications().getId()};
//        body = mf.format(args);


        URL resource = getClass().getResource("/reports/letter.jasper");
        HashMap param = new HashMap();
        param.put("appName", product.getApplicant().getAppName());
        param.put("prodName", product.getProdName());
        param.put("subject", "Product Registration for  " + product.getProdName() + " recieved");
//                + letter.getSubject() + " " + product.getProdName() + " ");
//        param.put("body", body);
        param.put("body", "Thank you for applying to register " + product.getProdName() + " manufactured by " + product.getApplicant().getAppName()
                + ". Your application is successfully submitted and the application number is " + product.getProdApplications().getProdAppNo() + ". "
                + "Please use this application number for any future correspondence.");
        param.put("address1", product.getApplicant().getAddress().getAddress1());
        param.put("address2", product.getApplicant().getAddress().getAddress2());
        param.put("country", product.getApplicant().getAddress().getCountry().getCountryName());
        param.put("registrar", "Major General Md Jahangir Hossain Mollik");
        return JasperFillManager.fillReport(resource.getFile(), param);
    }

    public JasperPrint generateDeficiency(List<ProdAppChecklist> prodAppChecklists, String comment, User user) throws JRException {

        String emailBody = getEmailBody(prodAppChecklists, comment);

        ProdAppChecklist prodAppChecklist = prodAppChecklists.get(0);
        ProdApplications prodApplications = prodAppChecklist.getProdApplications();
        Product product = productService.findProduct(prodApplications.getProd().getId());

        URL resource = getClass().getResource("/reports/deficiency.jasper");
        HashMap param = new HashMap();
        param.put("appName", product.getApplicant().getAppName());
        param.put("prodName", product.getProdName());
        param.put("prodStrength", product.getDosStrength()+product.getDosUnit());
        param.put("dosForm", product.getDosForm().getDosForm());
        param.put("manufName", product.getManufName());
        param.put("appType", "New Medicine Registration");
        param.put("subject", "Product application deficiency letter for  " + product.getProdName());
        param.put("body", emailBody);
        param.put("address1", product.getApplicant().getAddress().getAddress1());
        param.put("address2", product.getApplicant().getAddress().getAddress2());
        param.put("country", product.getApplicant().getAddress().getCountry().getCountryName());
        param.put("cso",user.getName());
        param.put("date", new Date());
        param.put("summary", comment);
        param.put("appNumber", prodApplications.getProdAppNo());
        return JasperFillManager.fillReport(resource.getFile(), param);
    }

    private String getEmailBody(List<ProdAppChecklist> prodAppChecklists, String summary) {
        String emailBody;

        emailBody = "<p>The following deficiency were found by module</p>";

        if(prodAppChecklists!=null) {
            emailBody += "<p><ul>";
            for (ProdAppChecklist papp : prodAppChecklists) {
                if (papp.isSendToApp()&&papp.getChecklist().isHeader()) {
                    emailBody += "<li>";
                    emailBody += "<p><b>"+papp.getChecklist().getModuleNo()+": "+papp.getChecklist().getName()+"</b>";
                    emailBody += "<br>"+papp.getAppRemark()+"</p>";
                    emailBody += "</li>";
                }
            }
            emailBody += "</ul></p>";
        }
        return emailBody;
    }

    public JasperPrint generateSampleRequest(Product product, User user) throws JRException {
        URL resource = getClass().getResource("/reports/sample_request.jasper");
        HashMap param = new HashMap();
        param.put("appName", product.getApplicant().getAppName());
        param.put("prodName", product.getProdName());
        param.put("prodStrength", product.getDosStrength()+product.getDosUnit());
        param.put("dosForm", product.getDosForm().getDosForm());
        param.put("manufName", product.getManufName());
        param.put("appType", "New Medicine Registration");
        param.put("subject", "Sample request letter for  " + product.getProdName());
        param.put("address1", product.getApplicant().getAddress().getAddress1());
        param.put("address2", product.getApplicant().getAddress().getAddress2());
        param.put("country", product.getApplicant().getAddress().getCountry().getCountryName());
        param.put("cso",user.getName());
        param.put("date", new Date());
        param.put("appNumber", product.getProdApplications().getProdAppNo());
        return JasperFillManager.fillReport(resource.getFile(), param);
    }
}