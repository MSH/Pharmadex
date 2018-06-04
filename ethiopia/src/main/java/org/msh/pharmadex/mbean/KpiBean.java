package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.service.GlobalEntityLists;
import org.msh.pharmadex.service.KpiService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Одиссей on 11.01.2017.
 */
@ManagedBean
@ViewScoped
public class KpiBean {
    private Date startDate;
    private Date endDate;
    private User employee;
    private List<User> employees;
    private String reportType = "Please, choose report type in sidebar menu...";
    private String reportKind = "0";
    private String department = "";
    private List<AppTiming> results3 = new ArrayList<AppTiming>();
    private List<AppOverall> results1 = new ArrayList<AppOverall>();
    private List<AppOverallReg> results1r = new ArrayList<AppOverallReg>();
    private FacesContext facesContext = FacesContext.getCurrentInstance();
    private boolean executorVisible = false;

    @ManagedProperty(value = "#{kpiService}")
    KpiService kpiService;

    @ManagedProperty(value = "#{globalEntityLists}")
    GlobalEntityLists globalEntityLists;

    public void startRepo() {
        String fldList = "";
        if (startDate == null)
            fldList = "start date";
        if (endDate == null)
            fldList = "".equals(fldList) ? "end date" : fldList + ", end date";
        if (!"".equals(fldList)) {
            facesContext.addMessage(null, new FacesMessage("start date"));
            return;
        }
        if ("List of Applications, Status and Processing Time by Employee".equals(reportType)) {
               if (employee!=null)
                   if ("RD".equals(department)) {
                       results3 = kpiService.makeAppStatusReportReg(startDate, endDate, employee.getUserId());
                   }else {
                       results3 = kpiService.makeAppStatusReportCSD(startDate, endDate, employee.getUserId());
                   }
               else{
                   facesContext.addMessage(null, new FacesMessage("piprepo_chooseemployee"));
                   return;
               }
        } else if ("Overall Application Status and Processing Time Customer Service Directorate".equals(reportType)) {
            Long employeeId = null;
            if (employee != null) employeeId = employee.getUserId();
            results3 = kpiService.makeAppStatusReportReg(startDate, endDate,employeeId);
        } else if ("Overall Application Status and Processing Time Registration Directorate".equals(reportType)) {
            results1r = kpiService.createRegReport12(startDate, endDate,null);
        } else if ("Applications Status by Employee for Registration Directorate".equals(reportType)) {
            Long employeeId = null;
            if (employee != null) employeeId = employee.getUserId();
            results1r = kpiService.createRegReport12(startDate, endDate, employeeId);
        } else if ("Applications Status by Employee for Customer Service Directorate".equals(reportType)) {
            Long employeeId = null;
            if (employee != null) employeeId = employee.getUserId();
            results1 = kpiService.createCSDReport2(startDate, endDate, employeeId);
        }
    }

    private void prepareReportsDate() {
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startDate = startCal.getTime();
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR, 23);
        endCal.set(Calendar.MINUTE, 59);
        endDate = endCal.getTime();
        results1 = new ArrayList<AppOverall>();
        results3 = new ArrayList<AppTiming>();
    }

    public void prepareReport2CSD() {
        prepareReportsDate();
        executorVisible = true;
        employee = null;
        reportType = "Applications Status by Employee for Customer Service Directorate";
        reportKind = "1";
        department = "CSD";
    }

    public void prepareReport2Reg() {
        prepareReportsDate();
        executorVisible = true;
        employee = null;
        reportType = "Applications Status by Employee for Registration Directorate";
        reportKind = "1R";
        department = "RD";
    }

    public void prepareReport3Reg() {
        prepareReportsDate();
        executorVisible = true;
        employee = null;
        reportType = "List of Applications, Status and Processing Time by Employee";
        department = "RD";
        reportKind = "3";
    }

    public void prepareReport3CSD() {
        prepareReportsDate();
        executorVisible = true;
        employee = null;
        reportType = "List of Applications, Status and Processing Time by Employee";
        department = "CSD";
        reportKind = "3";
    }

    public void prepareReport1CSD() {
        prepareReportsDate();
        executorVisible = false;
        employee = null;
        reportType = "Overall Application Status and Processing Time Customer Service Directorate";
        reportKind = "1";
        department = "CSD";
    }


    public void prepareReport1RegD() {
        prepareReportsDate();
        executorVisible = false;
        employee = null;
        reportType = "Overall Application Status and Processing Time Registration Directorate";
        reportKind = "1R";
        department = "RD";
    }

    /*
    public List<User> getEmployees() {
        if ("CSO".equalsIgnoreCase(department))
            employee = globalEntityLists.completeCSOList();
        else
        return employees;
    }
    */

    public void setEmployees(List<User> employees) {
        this.employees = employees;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public GlobalEntityLists getGlobalEntityLists() {
        return globalEntityLists;
    }

    public void setGlobalEntityLists(GlobalEntityLists globalEntityLists) {
        this.globalEntityLists = globalEntityLists;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public KpiService getKpiService() {
        return kpiService;
    }

    public List<AppTiming> getResults3() {
        return results3;
    }

    public void setResults3(List<AppTiming> results3) {
        this.results3 = results3;
    }

    public void setKpiService(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public boolean isExecutorVisible() {
        return executorVisible;
    }

    public void setExecutorVisible(boolean executorVisible) {
        this.executorVisible = executorVisible;
    }

    public String getReportKind() {
        return reportKind;
    }

    public void setReportKind(String reportKind) {
        this.reportKind = reportKind;
    }

    public List<AppOverall> getResults1() {
        return results1;
    }

    public void setResults1(List<AppOverall> results1) {
        this.results1 = results1;
    }

    public List<AppOverallReg> getResults1r() {
        return results1r;
    }

    public void setResults1r(List<AppOverallReg> results1r) {
        this.results1r = results1r;
    }
}

