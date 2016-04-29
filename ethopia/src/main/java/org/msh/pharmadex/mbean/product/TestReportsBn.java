package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.SuspDetail;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.SuspendService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Odissey on 19.04.2016.
 */
@ManagedBean
@ViewScoped
public class TestReportsBn {

    private String recId;

    @ManagedProperty(value = "#{prodApplicationsService}")
    private ProdApplicationsService prodApplicationsService;

    @ManagedProperty(value = "#{suspendService}")
    private SuspendService suspendService;


    public void startCheckingRegCertificate(){
        long id = Long.parseLong(recId);
        ProdApplications prodApp = prodApplicationsService.findProdApplications(id);
        try {
            prodApplicationsService.createRegCert(prodApp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createLetter() {
        long id = Long.parseLong(recId);
        SuspDetail sd = suspendService.findSuspendDetail(id);
        try {
            sd.setDecision(RecomendType.CANCEL);
            suspendService.saveSuspend(sd);
            suspendService.setSuspDetail(sd, (long) 1);
            suspendService.createCancelLetter();
            suspendService.createCancelSenderLetter();

            sd.setDecision(RecomendType.SUSPEND);
            suspendService.saveSuspend(sd);
            suspendService.setSuspDetail(sd, (long) 1);
            suspendService.createSuspLetter();
            suspendService.createCancelSenderLetter();

        } catch (IOException e) {
            e.printStackTrace();
        }  catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getRecId() {
        return recId;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    public ProdApplicationsService getProdApplicationsService() {
        return prodApplicationsService;
    }

    public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
        this.prodApplicationsService = prodApplicationsService;
    }

    public SuspendService getSuspendService() {
        return suspendService;
    }

    public void setSuspendService(SuspendService suspendService) {
        this.suspendService = suspendService;
    }


}
