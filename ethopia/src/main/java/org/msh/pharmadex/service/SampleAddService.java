package org.msh.pharmadex.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wing on 07.05.2016.
 */
@Service
public class SampleAddService implements Serializable {
    @Autowired
    private SampleTestDAO sampleTestDAO;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    private GlobalEntityLists globalEntityLists;
    @Autowired
    SampleTestService sampleTestService;
    @PersistenceContext
    private EntityManager entityManager;
    public JasperPrint initLetter(ProdApplications prodApplications, SampleComment sampleComment, String quantity) throws JRException, SQLException {
        String emailBody = sampleComment.getComment();
        Product product = prodApplications.getProduct();
        URL resource = getClass().getResource("/reports/sample_request_add.jasper");
        Connection conn = entityManager.unwrap(Session.class).connection();
        HashMap param = new HashMap();
        param.put("sampleQty", quantity);
        param.put("date", new Date());
        param.put("reason", sampleComment);
       if(resource != null){
            JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
            conn.close();
            return jasperPrint;
        } else{
            conn.close();
            return null;
        }
    }
    public RetObject createDefADDLetter(SampleTest sampleTest){
        ProdApplications prodApp = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
        Product product = prodApp.getProduct();
        try {
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_addsamplerequest", ".pdf");
            JasperPrint jasperPrint = initLetter(prodApp, sampleTest.getSampleComments().get(0), sampleTest.getQuantity());
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(invoicePDF));
            byte[] file = IOUtils.toByteArray(new FileInputStream(invoicePDF));
            ProdAppLetter attachment = new ProdAppLetter();
            attachment.setRegState(prodApp.getRegState());
            attachment.setFile(file);
            attachment.setProdApplications(prodApp);
            attachment.setFileName(invoicePDF.getName());
            attachment.setTitle("Additional Sample Request Letter");
            attachment.setUploadedBy(sampleTest.getCreatedBy());
            attachment.setComment("System generated Letter");
            attachment.setLetterType(LetterType.SAMPLE_REQUEST_LETTER);
            attachment.setContentType("application/pdf");
            if(sampleTest.getProdAppLetters()==null)
                sampleTest.setProdAppLetters(new ArrayList<ProdAppLetter>());
            sampleTest.getProdAppLetters().add(attachment);
            sampleTestDAO.saveAndFlush(sampleTest);
            return sampleTestService.saveSample(sampleTest);
        } catch (JRException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new RetObject("error");
        } catch (SQLException e) {
            e.printStackTrace();
            return new RetObject("error");
        }
    }
}
