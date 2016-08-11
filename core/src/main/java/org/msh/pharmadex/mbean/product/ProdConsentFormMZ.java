/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.mbean.product;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.LetterType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;
import org.msh.pharmadex.service.ReportService;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Backing bean to capture review of products
 * Author: usrivastava
 */
@ManagedBean
@RequestScoped
public class ProdConsentFormMZ implements Serializable {

    java.util.ResourceBundle bundle;
    @ManagedProperty(value = "#{userSession}")
    private UserSession userSession;
    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;
    @ManagedProperty(value = "#{prodApplicationsServiceMZ}")
    private ProdApplicationsServiceMZ prodApplicationsServiceMZ;
    @ManagedProperty(value = "#{userService}")
    private UserService userService;
    @ManagedProperty(value = "#{reportService}")
    private ReportService reportService;
    @ManagedProperty(value = "#{timelineService}")
    private TimelineService timeLineService;
    
    @ManagedProperty(value = "#{prodAckLetterFormMZ}")
    private ProdAckLetterFormMZ prodAckLetterFormMZ;
    
    private String password;
    private Product product;
    private ProdApplications prodApplications;
    private FacesContext context;
    private JasperPrint jasperPrint;
    private UploadedFile file;
    
    //private String sender = "";
    //private String prodappno = "";

    public StreamedContent fileDownload() {
        ProdAppLetter prodAppLetter = prodApplicationsService.findAllLettersByProdAppAndType(prodApplications, LetterType.ACK_SUBMITTED);
        if (prodAppLetter != null) {
            InputStream ist = new ByteArrayInputStream(prodAppLetter.getFile());
            StreamedContent download = new DefaultStreamedContent(ist, prodAppLetter.getContentType(), prodAppLetter.getFileName());
//        StreamedContent download = new DefaultStreamedContent(ist, "image/jpg", "After3.jpg");
            return download;
        } else {

            return null;
        }
    }


    @PostConstruct
    private void init() {
    	context = FacesContext.getCurrentInstance();
        bundle = context.getApplication().getResourceBundle(context, "msgs");
        
        Long prodAppID = userSession.getProdAppID();
        if (prodAppID != null) {
            prodApplications = prodApplicationsService.findProdApplications(prodAppID);
            
            getProdAckLetterFormMZ().initParametrs(prodApplications);
           // getProdAckLetterFormMZ().setProdappno(prodApplicationsService.generateAppNo(prodApplications));
           // getProdAckLetterFormMZ().setSender(bundle.getString("gestorDeCTRM_value"));
            product = prodApplications.getProduct();
        }

    }


    @Transactional
    public String submitApp() {
        context = FacesContext.getCurrentInstance();
        bundle = context.getApplication().getResourceBundle(context, "msgs");
        User user = userService.findUser(userSession.getLoggedINUserID());

        if (!userService.verifyUser(userSession.getLoggedINUserID(), password)) {
            context.addMessage(null, new FacesMessage(bundle.getString("app_submit_success")));
        }
        
        getProdAckLetterFormMZ().buildParametrs(prodApplications);
        /*if(getProdAckLetterFormMZ().getProdappno() == null  || getProdAckLetterFormMZ().getProdappno().equals(""))
        	getProdAckLetterFormMZ().setProdappno(prodApplicationsService.generateAppNo(prodApplications));
        prodApplications.setProdAppNo(getProdAckLetterFormMZ().getProdappno());
        prodApplications.setUsername(getProdAckLetterFormMZ().getSender());*/
       // if (prodApplications.getProdAppNo() == null || prodApplications.getProdAppNo().equals(""))
          //  prodApplications.setProdAppNo(prodApplicationsService.generateAppNo(prodApplications));

        TimeLine timeLine = new TimeLine();
        timeLine.setComment(bundle.getString("timeline_newapp"));
        timeLine.setRegState(RegState.NEW_APPL);
        timeLine.setProdApplications(prodApplications);
        timeLine.setUser(user);
        timeLine.setStatusDate(new Date());
        
        prodApplications.setRegState(RegState.NEW_APPL);
        prodApplications.setSubmitDate(new Date());
        RetObject retObject = prodApplicationsService.submitProdApp(prodApplications, userSession.getLoggedINUserID());
        if (retObject.getMsg().equals("persist")) {
            prodApplications = (ProdApplications) retObject.getObj();
            prodApplicationsServiceMZ.createAckLetter(prodApplications);
            timeLine.setProdApplications(prodApplications);
            timeLineService.saveTimeLine(timeLine);
            context.addMessage(null, new FacesMessage(bundle.getString("app_submit_success")));
            userSession.setProdAppID(prodApplications.getId());
            return "/internal/processreg.faces";
        } else {
            context.addMessage(null, new FacesMessage(bundle.getString("global_fail")));
            return "";
        }
    }

    public void PDF() throws JRException, IOException {
        context = FacesContext.getCurrentInstance();
        jasperPrint = reportService.reportinit(prodApplications);
        javax.servlet.http.HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
        httpServletResponse.setContentType("application/pdf");
        javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);

    }

    public String cancel() {
        return "/public/registrationhome.faces";
    }


    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }
    
    public ProdApplicationsServiceMZ getProdApplicationsServiceMZ() {
        return prodApplicationsServiceMZ;
    }

    public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ prodApplicationsServiceMZ) {
        this.prodApplicationsServiceMZ = prodApplicationsServiceMZ	;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public TimelineService getTimeLineService() {
        return timeLineService;
    }

    public void setTimeLineService(TimelineService timeLineService) {
        this.timeLineService = timeLineService;
    }

	public ProdAckLetterFormMZ getProdAckLetterFormMZ() {
		return prodAckLetterFormMZ;
	}


	public void setProdAckLetterFormMZ(ProdAckLetterFormMZ prodAckLetterFormMZ) {
		this.prodAckLetterFormMZ = prodAckLetterFormMZ;
	}

	/*public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getProdappno() {
		return prodappno;
	}

	public void setProdappno(String prodappno) {
		this.prodappno = prodappno;
	}*/
}
