package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

@Service
public class ReportService implements Serializable {

    @Autowired
    LetterService letterService;


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
        param.put("subject", "Product Registration for  "+product.getProdName()+" recieved");
//                + letter.getSubject() + " " + product.getProdName() + " ");
//        param.put("body", body);
        param.put("body", "Thank you for applying to register " + product.getProdName() + " manufactured by " + product.getApplicant().getAppName()
                + ". Your application is successfully submitted and the application number is " + product.getProdApplications().getProdAppNo() + ". "
                + "Please use this application number for any future correspondence.");
        param.put("address1", product.getApplicant().getAddress().getAddress1());
        param.put("address2", product.getApplicant().getAddress().getAddress2());
        param.put("country", product.getApplicant().getAddress().getCountry().getCountryName());
        param.put("registrar", "Johannes");
        return JasperFillManager.fillReport(resource.getFile(), param);
    }
}