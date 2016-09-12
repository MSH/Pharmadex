package org.msh.pharmadex.mbean.product;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.msh.pharmadex.domain.ProdApplications;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Author: dudchenko
 */
@ManagedBean
@ViewScoped
public class FileUploadControllerMZ implements Serializable {

	private static final long serialVersionUID = 4527601943158238740L;
	
	@ManagedProperty(value = "#{processProdBnMZ}")
    private ProcessProdBnMZ processProdBnMZ;
	
	private StreamedContent regcert = null;
	private StreamedContent rejectcert = null;

    public StreamedContent getRegcert() throws SQLException, IOException, JRException {
        ProdApplications prodApplications = processProdBnMZ.findProdApplications();
        if(prodApplications != null && prodApplications.getRegCert() != null){
	        InputStream ist = new ByteArrayInputStream(prodApplications.getRegCert());
	        Calendar c = Calendar.getInstance();
	        regcert = new DefaultStreamedContent(ist, "pdf", "registration_" + prodApplications.getId() + "_" + c.get(Calendar.YEAR)+".pdf");
        }
        return regcert;
    }
    
    public StreamedContent getRejectcert() throws SQLException, IOException, JRException {
        ProdApplications prodApplications = processProdBnMZ.findProdApplications();
        if(prodApplications != null && prodApplications.getRejCert() != null){
	        InputStream ist = new ByteArrayInputStream(prodApplications.getRejCert());
	        Calendar c = Calendar.getInstance();
	        rejectcert = new DefaultStreamedContent(ist, "pdf", "rejection_" + prodApplications.getId() + "_" + c.get(Calendar.YEAR)+".pdf");
        }
        return rejectcert;
    }
    
    public ProcessProdBnMZ getProcessProdBnMZ() {
        return processProdBnMZ;
    }

    public void setProcessProdBnMZ	(ProcessProdBnMZ process) {
        this.processProdBnMZ = process;
    }
}
