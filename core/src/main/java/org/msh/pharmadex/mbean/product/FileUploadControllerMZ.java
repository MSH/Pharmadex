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

    public StreamedContent regCertDownload() throws SQLException, IOException, JRException {
        ProdApplications prodApplications = processProdBnMZ.findProdApplications();
        if(prodApplications != null){
	        InputStream ist = new ByteArrayInputStream(prodApplications.getRegCert());
	        Calendar c = Calendar.getInstance();
	        StreamedContent download = new DefaultStreamedContent(ist, "pdf", "registration_" + prodApplications.getId() + "_" + c.get(Calendar.YEAR)+".pdf");
	        return download;
        }
        return null;
    }
    
    public ProcessProdBnMZ getProcessProdBnMZ() {
        return processProdBnMZ;
    }

    public void setProcessProdBnMZ	(ProcessProdBnMZ process) {
        this.processProdBnMZ = process;
    }
}
