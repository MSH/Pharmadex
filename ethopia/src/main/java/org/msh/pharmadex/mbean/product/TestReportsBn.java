package org.msh.pharmadex.mbean.product;

import net.sf.jasperreports.engine.JRException;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.SuspDetail;
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

    public SuspendService getSuspendServiceET() {
        return suspendServiceET;
    }

    public void setSuspendServiceET(SuspendService suspendServiceET) {
        this.suspendServiceET = suspendServiceET;
    }

    @ManagedProperty(value = "#{suspendServiceET}")
    private SuspendService suspendServiceET;

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

public void createLetter() {
    long id = Long.parseLong(recId);
    SuspDetail sd = suspendServiceET.findSuspendDetail(id);

    suspendServiceET.setSuspDetail(sd, (long) 1);
    try {
        suspendServiceET.createSuspLetter();
    } catch (IOException e) {
        e.printStackTrace();
    }  catch (JRException e) {
        e.printStackTrace();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
