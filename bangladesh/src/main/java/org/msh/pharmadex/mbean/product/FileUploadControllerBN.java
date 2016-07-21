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
public class FileUploadControllerBN implements Serializable {

	private static final long serialVersionUID = 8509206663479692753L;
	@ManagedProperty(value = "#{processProdBnBN}")
    private ProcessProdBnBN processProdBnBN;

    public StreamedContent regCertDownload() throws SQLException, IOException, JRException {
        ProdApplications prodApplications = processProdBnBN.findProdApplications();
        if(prodApplications != null){
	        InputStream ist = new ByteArrayInputStream(prodApplications.getRegCert());
	        Calendar c = Calendar.getInstance();
	        StreamedContent download = new DefaultStreamedContent(ist, "pdf", "registration_" + prodApplications.getId() + "_" + c.get(Calendar.YEAR)+".pdf");
	        return download;
        }
        return null;
    }
    
    public ProcessProdBnBN getProcessProdBnBN() {
        return processProdBnBN;
    }

    public void setProcessProdBnBN	(ProcessProdBnBN process) {
        this.processProdBnBN = process;
    }
}
