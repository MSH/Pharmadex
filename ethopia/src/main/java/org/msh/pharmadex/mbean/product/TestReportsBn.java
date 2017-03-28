package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.SuspDetail;
import org.msh.pharmadex.domain.enums.RecomendType;
import org.msh.pharmadex.service.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by Odissey on 19.04.2016.
 * User: Odissey
 */
@ManagedBean
@ViewScoped
public class TestReportsBn {

    private String recId;

    @ManagedProperty(value = "#{prodApplicationsServiceET}")
    private ProdApplicationsServiceET prodApplicationsServiceET;

    @ManagedProperty(value = "#{suspendService}")
    private SuspendService suspendService;

    @ManagedProperty(value = "#{scheduler}")
    private Scheduler scheduler;

    @ManagedProperty(value = "#{kpiService}")
    private KpiService kpiService;


    public void testNotification(){
        scheduler.processNotifications();
    }

    public void startCheckingRegCertificate(){
        long id = Long.parseLong(recId);
        ProdApplications prodApp = prodApplicationsServiceET.findProdApplications(id);
        prodApplicationsServiceET.createRegCert(prodApp);
        prodApplicationsServiceET.createAckLetter(prodApp);
    }

    public void testQuery(){
        Calendar yesterday = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH,-6);
        today.add(Calendar.DAY_OF_MONTH,-1);
        yesterday.set(Calendar.HOUR_OF_DAY,0);
        yesterday.set(Calendar.MINUTE,0);
        today.set(Calendar.HOUR_OF_DAY,23);
        today.set(Calendar.MINUTE,59);
        kpiService.makeAppStatusReportCSD(yesterday.getTime(),today.getTime(), (long) 297);
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

    public ProdApplicationsServiceET getProdApplicationsServiceET() {
        return prodApplicationsServiceET;
    }

    public void setProdApplicationsServiceET(ProdApplicationsServiceET prodApplicationsServiceET) {
        this.prodApplicationsServiceET = prodApplicationsServiceET;
    }

    public SuspendService getSuspendService() {
        return suspendService;
    }

    public void setSuspendService(SuspendService suspendService) {
        this.suspendService = suspendService;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public KpiService getKpiService() {
        return kpiService;
    }

    public void setKpiService(KpiService kpiService) {
        this.kpiService = kpiService;
    }
}
