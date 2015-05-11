package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnET implements Serializable {


    private Logger logger = LoggerFactory.getLogger(ProcessProdBn.class);
    private JasperPrint jasperPrint;

    @ManagedProperty(value = "#{processProdBn}")
    public ProcessProdBn processProdBn;

    public void prescreenfeerecvd(){
        processProdBn.prodApplications.setPrescreenfeeReceived(true);
    }

    public void changeStatusListener() {
        logger.error("Inside changeStatusListener");
        processProdBn.save();
        ProdApplications prodApplications = processProdBn.getProdApplications();
        TimeLine timeLine = new TimeLine();

        if (prodApplications.getRegState().equals(RegState.NEW_APPL)) {
            timeLine.setRegState(RegState.FEE);
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();
        }
        if (prodApplications.getRegState().equals(RegState.FEE)) {
            if (prodApplications.isApplicantVerified() && prodApplications.isProductVerified() && prodApplications.isDossierReceived()) {
                timeLine.setRegState(RegState.VERIFY);
                processProdBn.setTimeLine(timeLine);
                processProdBn.addTimeline();
            }
        }
        if (prodApplications.getRegState().equals(RegState.SCREENING)) {
            timeLine.setRegState(RegState.FEE)  ;
            processProdBn.setTimeLine(timeLine);
            processProdBn.addTimeline();

        }

        processProdBn.setSelectedTab(2);
    }

    public ProcessProdBn getProcessProdBn() {
        return processProdBn;
    }

    public void setProcessProdBn(ProcessProdBn processProdBn) {
        this.processProdBn = processProdBn;
    }
}
