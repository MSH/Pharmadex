package org.msh.pharmadex.mbean;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.service.*;
import org.msh.pharmadex.util.JsfUtils;
import org.primefaces.extensions.component.timeline.Timeline;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Backing bean to process the application made for registration
 * Author: usrivastava
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnET extends ProcessProdBn implements Serializable {



    public String completeScreen(){
        if (getProdApplications().getRegState().equals(RegState.NEW_APPL)) {
            timeLine = new TimeLine();
            timeLine.setRegState(RegState.SCREENING);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(getUserSession().getLoggedInUserObj());
            timeLine.setComment("Pre-Screening completed successfully");
            setTimeLine(timeLine);
            addTimeline();
        }
        return "";
    }

    @Override
    public String addTimeline() {
        facesContext = FacesContext.getCurrentInstance();

        try {
            timeLine.setProdApplications(getProdApplications());
            timeLine.setStatusDate(new Date());
            timeLine.setUser(userSession.getLoggedInUserObj());
            String retValue = timelineService.validateStatusChange(timeLine);

            if (retValue.equalsIgnoreCase("success")) {
                timeLineList.add(timeLine);
                prodApplications.setRegState(timeLine.getRegState());
                product.setRegState(timeLine.getRegState());
                prodApplications = prodApplicationsService.updateProdApp(prodApplications);
                product = productService.findProduct(prodApplications.getProd().getId());
                setFieldValues();
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
            } else if (retValue.equalsIgnoreCase("fee_not_recieved")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            } else if (retValue.equalsIgnoreCase("app_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
            } else if (retValue.equalsIgnoreCase("prod_not_verified")) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), resourceBundle.getString("prod_not_verified")));
            } else if (retValue.equalsIgnoreCase("valid_assign_moderator")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_moderator")));
            } else if (retValue.equalsIgnoreCase("valid_assign_reviewer")) {
                facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_reviewer")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
        }
        timeLine = new TimeLine();
        return "";  //To change body of created methods use File | Settings | File Templates.
    }


    public String sendToApplicant(){
            TimeLine timeLine = getTimeLine();
            timeLine = new TimeLine();
            timeLine.setRegState(RegState.FOLLOW_UP);
            timeLine.setStatusDate(new Date());
            timeLine.setUser(getUserSession().getLoggedInUserObj());
            setTimeLine(timeLine);
            addTimeline();
        return "";
    }

    public String archiveApp(){
        TimeLine timeLine = getTimeLine();
        timeLine = new TimeLine();
        timeLine.setRegState(RegState.ARCHIVE);
        timeLine.setStatusDate(new Date());
        timeLine.setUser(getUserSession().getLoggedInUserObj());
        setTimeLine(timeLine);
        addTimeline();
        return "";

    }

}
