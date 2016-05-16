package org.msh.pharmadex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.SampleType;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Created by wing on 07.05.2016.
 */
@Service
public class SampleAddService implements Serializable {

	private static final long serialVersionUID = 2492127394907263805L;

	@Autowired
    private SampleTestDAO sampleTestDAO;

    @Autowired
    private ProdApplicationsService prodApplicationsService;

    @Autowired
    SampleTestService sampleTestService;
    @PersistenceContext
    private EntityManager entityManager;

    private ResourceBundle resourceBundle = null;

    public JasperPrint initLetter(ProdApplications prodApplications, SampleTest sampleTest) throws JRException, SQLException {
    	if(prodApplications == null)
    		return null;
    	if(sampleTest == null)
    		return null;
    	Product product = prodApplications.getProduct();
     	if(product == null)
     		return null;
     	
    	List<SampleType> items = sampleTest.getSampleTypes();
    	String itemsStr = "";
    	if(items != null && items.size() > 0){
    		for(SampleType st:items){
    			itemsStr += resourceBundle.getString(st.getKey()) + ", ";
    		}
    		itemsStr = itemsStr.substring(0, itemsStr.length() - 2);
    	}
    	String quantity = sampleTest.getQuantity();
    	String comment = "";
    	List<SampleComment> comms = sampleTest.getSampleComments();
    	if(comms != null && comms.size() > 0)
    		comment = comms.get(0).getComment();
    	
    	URL resource = getClass().getResource("/reports/sample_request_add.jasper");
        Connection conn = entityManager.unwrap(Session.class).connection();
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("sampleQty", quantity);
        param.put("id", prodApplications.getId());
        
        String manStr = product.getManufName() != null ? product.getManufName():"";
    	param.put("manufName", manStr);
    	
        param.put("itemsReq", itemsStr);
        param.put("comment", comment);

       if(resource != null){
            JasperPrint jasperPrint = JasperFillManager.fillReport(resource.getFile(), param, conn);
            conn.close();
            return jasperPrint;
        } else{
            conn.close();
            return null;
        }
    }
    
    public RetObject createDefADDLetter(SampleTest sampleTest, ResourceBundle resourceBundle){
    	this.resourceBundle = resourceBundle;
    	
        ProdApplications prodApp = prodApplicationsService.findProdApplications(sampleTest.getProdApplications().getId());
        Product product = prodApp.getProduct();
        try {
            File invoicePDF = File.createTempFile("" + product.getProdName() + "_addsamplerequest", ".pdf");
            JasperPrint jasperPrint = initLetter(prodApp, sampleTest);
            		//initLetter(prodApp, sampleTest.getSampleComments().get(0), sampleTest.getQuantity());
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
