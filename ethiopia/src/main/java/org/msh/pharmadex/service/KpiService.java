/*
 * Copyright (c) 2014. Management Sciences for Health. All Rights Reserved.
 */

package org.msh.pharmadex.service;

import org.apache.commons.lang3.time.DateUtils;
import org.msh.pharmadex.dao.CustomTimelineDAO;
import org.msh.pharmadex.dao.iface.TimelineDAO;
import org.msh.pharmadex.domain.enums.ProdAppType;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.AppOverall;
import org.msh.pharmadex.mbean.AppTiming;
import org.msh.pharmadex.mbean.AppOverallReg;
import org.msh.pharmadex.util.StrTools;
import org.msh.pharmadex.utils.RepoAppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: Odissey
 */
@Service
public class KpiService implements Serializable {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CustomTimelineDAO customTimelineDAO;
    @Autowired
    TimelineDAO timelineDAO;

    @Autowired
    UserService userService;

    int laborTimeStartMins;
    int laborTimeStartHours;
    int laborTimeEndMins;
    int laborTimeEndHours;


    @PostConstruct
    private void init(){
        Properties prop = new Properties();
        try {
            String laborTimeStart;
            String laborTimeEnd;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            prop.load(loader.getResourceAsStream("db.properties"));
            laborTimeStart = prop.getProperty("labor.time.start");
            laborTimeEnd = prop.getProperty("labor.time.end");
            laborTimeStartMins = Integer.parseInt(laborTimeStart.split(":")[1]);
            laborTimeStartHours = Integer.parseInt(laborTimeStart.split(":")[0]);
            laborTimeEndMins = Integer.parseInt(laborTimeEnd.split(":")[1]);
            laborTimeEndHours = Integer.parseInt(laborTimeEnd.split(":")[0]);
        } catch (IOException e) {
            String laborTimeStart="8:30";
            String laborTimeEnd="17:30";
        }

    }

    public List<AppTiming> makeAppStatusReportReg(Date startDate, Date endDate, Long employeeId) {
        List<AppTiming> res = new ArrayList<AppTiming>();
        List<Object[]> apps = null;
        Date docCreationDate = startDate;
        apps = customTimelineDAO.findApplicationByEmployee(startDate, endDate, employeeId);
        if (apps.size() != 0) {
            for (int i = 0; i < apps.size(); i++) {
                Object[] retValue = apps.get(i);
                BigInteger appId = (BigInteger) retValue[0];
                List timeLines = customTimelineDAO.findEventsByApplication(startDate, endDate, appId.longValue());
                if (timeLines != null) {
                    AppTiming timing = calculateTimingReg(timeLines, String.valueOf(employeeId));
                    if (timing != null)
                        res.add(timing);
                }
            }
        }

        return res;
    }

    public List<AppTiming> makeAppStatusReportCSD(Date startDate, Date endDate, Long employeeId) {
        List<AppTiming> res = new ArrayList<AppTiming>();
        List<Object[]> apps = null;
        Date docCreationDate = startDate;
        // Ordinal applications: register, renewal and variation
        apps = customTimelineDAO.findApplicationByEmployee(startDate, endDate, employeeId);
        if (apps.size() != 0) {
            for (int i = 0; i < apps.size(); i++) {
                Object[] retValue = apps.get(i);
                BigInteger appId = (BigInteger) retValue[0];
                List timeLines = customTimelineDAO.findEventsByApplication(startDate, endDate, appId.longValue());
                if (timeLines != null) {
                    AppTiming timing = calculateTiming(timeLines);
                    if (timing != null)
                        res.add(timing);
                }
            }
        }
        //PIP applications
        apps = customTimelineDAO.findOrderByExecutor(startDate, endDate, employeeId, "PIP");
        if (apps.size() != 0) {
            for (int i = 0; i < apps.size(); i++) {
                Object[] retValue = apps.get(i);
                List timeLines = findAllTimelinesOfOrder(startDate, endDate, employeeId, retValue, "PIP");
                if (timeLines != null) {
                    AppTiming timing = calculateTiming(timeLines);
                    if (timing != null)
                        res.add(timing);
                }
            }
        }
        //PO applications
        apps = customTimelineDAO.findOrderByExecutor(startDate, endDate, employeeId, "PO");
        if (apps.size() == 0) {
            for (int i = 0; i < apps.size(); i++) {
                Object[] retValue = apps.get(i);
                List timeLines = findAllTimelinesOfOrder(startDate, endDate, employeeId, retValue, "PO");
                if (timeLines != null) {
                    AppTiming timing = calculateTiming(timeLines);
                    if (timing != null)
                        res.add(timing);
                }
            }
        }
        return res;
    }


    private RepoAppEvent parseResultSet(Object[] rset) {
        RepoAppEvent e = new RepoAppEvent();
        e.eventTime = (Date) rset[2];
        e.status = (String) rset[1];
        e.appType = (String) rset[6];
        e.userId = ((BigInteger) rset[3]).toString(0);
        return e;
    }

    private RepoAppEvent findPreviousRecord(List timelines, int i, RepoAppEvent cur) {
        String status = cur.status;
        RepoAppEvent prev = null;
        Object[] record = null;
        String prevStatus = "";
        record = (Object[]) timelines.get(i);
        prev = parseResultSet(record);

        if ("PIP".equalsIgnoreCase(cur.appType) || "PO".equals(cur.appType)) {
            if ("REJECTED".equals(status)) {
                while (("NOT_RECOMMENDED".equals(prevStatus) || "RECOMMENDED".equals(prevStatus)) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                    record = (Object[]) timelines.get(i - 1);
                }
            } else if ("RECOMMENDED".equals(status)) {
                while (("NOT_RECOMMENDED".equals(prevStatus) || "RECOMMENDED".equals(prevStatus)) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
            } else if ("NOT_RECOMMENDED".equals(prevStatus) || "RECOMMENDED".equals(prevStatus)) {
                while ("REVIEW_BOARD".equals(prevStatus) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
            } else if ("REVIEW_BOARD".equals(prevStatus)) {
                while (("FOLLOW_UP".equals(prevStatus) || "NEW_APPL".equals(prevStatus)) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
            } else if ("FOLLOW_UP".equals(prevStatus)) {
                while (("REVIEW_BOARD".equals(prevStatus) || "NEW_APPL".equals(prevStatus)) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
            } else if ("NEW_APPL".equals(prevStatus)) {
                while ("FOLLOW_UP".equals(prevStatus) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
            }else if ("SCREENING".equals(prevStatus)) {
                while (("PRE_SCREENING".equals(prevStatus) || "NEW_APPL".equals(prevStatus)) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
            }
        } else if ("PA".equalsIgnoreCase(cur.appType)) {
            if ("REGISTERED".equals(status)) {
                while (("NOT_RECOMMENDED".equals(prevStatus) || "RECOMMENDED".equals(prevStatus)) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
            }else if ("SCREENING".equals(status))
                while (("PRE_SCREENING".equals(prevStatus) || "NEW_APPL".equals(prevStatus)) && i > 0) {
                    prev = parseResultSet(record);
                    prevStatus = prev.status;
                    i--;
                }
        }
        prev.index = i;
        return prev;
    }

    private AppTiming addReviewTime(AppTiming at, RepoAppEvent curEvnt, String userId, Double hours){
        Double curValue;
        if (curEvnt.userId.equals(userId)) {
            curValue = at.getUnderReview();
            at.setUnderReview(curValue + hours);
        }else {
            curValue = at.getWaiting();
            at.setWaiting(curValue + hours);
        }
        return at;
    }

    private AppTiming calculateTimingReg(List tls,String userId){
        if (tls == null) return null;
        if (tls.size() == 0) return null;
        AppTiming at = new AppTiming();
        int i = tls.size()-1;
        String appNo = "";
        boolean firstTime=true;
        while (i > 0) {
            Object[] tl = (Object[]) tls.get(i);
            appNo = (String) tl[4];
            at.setAppNo(appNo);
            RepoAppEvent cur = parseResultSet(tl);
            RepoAppEvent prev = findPreviousRecord(tls, i - 1, cur);
            i--;
            Double difference=0.0;
            if (!firstTime)
                difference = timeDifference(prev.eventTime, cur.eventTime);
            else {
                if (!("REJECTED".equalsIgnoreCase(cur.status)||("REGISTERED".equalsIgnoreCase(cur.status)))||("ARCHIVED".equalsIgnoreCase(cur.status)))
                    difference = timeDifference(cur.eventTime, Calendar.getInstance().getTime());
                else
                    difference = timeDifference(prev.eventTime, cur.eventTime);
                firstTime = false;
            }
            Double curValue = 0.0;
            if ("REJECTED".equalsIgnoreCase(cur.status)) {
                curValue = at.getRejected();
                at.setRejected(curValue + difference);
            } else if ("RECOMMENDED".equalsIgnoreCase(cur.status) || "NOT_RECOMMENDED".equalsIgnoreCase(cur.status)) {
                addReviewTime(at,cur,userId,difference);
            } else if ("REVIEW_BOARD".equalsIgnoreCase(cur.status)) {
                if (prev.status.equals("FOLLOW_UP")) {
                    curValue = at.getFir();
                    at.setFir(curValue + difference);
                }else {
                    addReviewTime(at,cur,userId,difference);
                }
            } else if ("FOLLOW_UP".equalsIgnoreCase(cur.status)) {
                if (prev.status.equals("REVIEW_BOARD")) {
                    addReviewTime(at,cur,userId,difference);
                }else{
                    curValue = at.getFir();
                    at.setFir(curValue + difference);
                }
            } else if ("REGISTERED".equalsIgnoreCase(cur.status)) {
                if (prev.status.equals("REVIEW_BOARD")) {//for old registration records
                    addReviewTime(at,cur,userId,difference);
                } else {
                    curValue = at.getApproved();
                    at.setApproved(curValue + difference);
                }
            } else if ("VERIFY".equals(cur.status) || "FEE".equals(cur.status)) {
                 curValue = at.getQueued();
                 at.setQueued(curValue + difference);
            } else if ("SCREENING".equals(cur.status)) {
                if (prev.status.equals("REVIEW_BOARD")) {
                    addReviewTime(at,cur,userId,difference);
                }
            } else if ("PRE_SCREENING".equals(cur.status)) {
                break; //all done, screening is not registration procedure
            } else if ("NEW_APPL".equalsIgnoreCase(cur.status)) {
                break; //all done, it is not registration procedure
            }
            at.setAppNo(appNo);
        }
        at.getTotal();
        return at;
    }

    private AppTiming calculateTiming(List tls) {
        if (tls == null) return null;
        if (tls.size() == 0) return null;
        AppTiming at = new AppTiming();
        int i = tls.size() - 1;
        String appNo = "";
        while (i > 0) {
            Object[] tl = (Object[]) tls.get(i);
            appNo = (String) tl[4];
            RepoAppEvent cur = parseResultSet(tl);
            RepoAppEvent prev = findPreviousRecord(tls, i - 1, cur);
            i = prev.index;
            Double difference = timeDifference(prev.eventTime, cur.eventTime);
            Double curValue = 0.0;
            if ("REJECTED".equalsIgnoreCase(cur.status)) {
                curValue = at.getRejected();
                at.setRejected(curValue + difference);
            } else if ("RECOMMENDED".equalsIgnoreCase(cur.status) || "NOT_RECOMMENDED".equalsIgnoreCase(cur.status)) {
                if ("PIP".equalsIgnoreCase(cur.appType) || "PO".equalsIgnoreCase(cur.appType)) {
                    curValue = at.getQueued();
                    at.setQueued(curValue + difference);
                }else{
                    curValue = at.getUnderReview();
                    at.setUnderReview(curValue + difference);
                }
            } else if ("REVIEW_BOARD".equalsIgnoreCase(cur.status)) {
                if (prev.status.equals("FOLLOW_UP")) {
                    curValue = at.getWaiting();
                    at.setWaiting(curValue + difference);
                } else {
                    curValue = at.getUnderReview();
                    at.setUnderReview(curValue + difference);
                }
            } else if ("FOLLOW_UP".equalsIgnoreCase(cur.status)) {
                curValue = at.getUnderReview();
                at.setUnderReview(curValue + difference);
            } else if ("REGISTERED".equalsIgnoreCase(cur.status)) {
                if (prev.status.equals("REVIEW_BOARD")) {//for old registration records
                    curValue = at.getUnderReview();
                    at.setUnderReview(curValue + difference);
                } else {
                    curValue = at.getApproved();
                    at.setApproved(curValue + difference);
                }
            } else if ("VERIFY".equals(cur.status) || "FEE".equals(cur.status)) {
                if (!("PIP".equalsIgnoreCase(cur.appType) || "PO".equalsIgnoreCase(cur.appType))) {
                    //for regular applications
                    curValue = at.getApproved();
                    at.setApproved(curValue + difference);
                }
            } else if ("SCREENING".equals(cur.status)) {
                curValue = at.getPre_screening();
                at.setPre_screening(curValue + difference);
            } else if ("NEW_APPL".equalsIgnoreCase(cur.status)) {
                if (prev.status.equals("FOLLOW_UP")) {
                    curValue = at.getWaiting();
                    at.setWaiting(curValue + difference);
                }
            }
        }
        at.setAppNo(appNo);
        at.getTotal();
        return at;
    }

    private List findAllTimelinesOfOrder(Date startDate, Date endDate, Long employeeId, Object[] resultSet, String orderType) {
        if (resultSet.length == 0) return null;
        BigInteger retId = (BigInteger) resultSet[0];
        Long appId = retId.longValue();
        if (appId == null) return null;
        List res = customTimelineDAO.findOrderByExecutorAndApp(startDate, endDate, employeeId, appId, orderType);
        if (res.size() == 0) return null;
        return res;
    }


    private boolean isSameDay(Calendar start, Calendar end){
        if ((start==null) && (end==null)) return true;
        if ((start==null) && (end!=null)) return false;
        if ((start!=null) && (end==null)) return false;
        if (start.get(Calendar.DAY_OF_MONTH)!=end.get(Calendar.DAY_OF_MONTH)){
            return false;
        }else if (start.get(Calendar.MONTH)!=end.get(Calendar.MONTH)) {
            return false;
        }else if (start.get(Calendar.YEAR)!=end.get(Calendar.YEAR)) {
            return false;
        }
        return true;
    }
    private Double timeDifference(Date startDate, Date endDate) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startDate);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);
        Calendar endOfDay = Calendar.getInstance();
        Double res = 0.0;
        Double thisdaydiff;
        if (isSameDay(calStart,calEnd)) {
            //same day - simple difference
            if (startDate == null && endDate == null) return 0.0;
            Double diff = Double.valueOf(endDate.getTime() - startDate.getTime());
            if (diff < 0) diff = diff * -1;
            res = Double.valueOf(diff / 1000 / 60 / 60);
            res = Math.round(res * 100.d) / 100.0d;
        }else{
            Calendar calCur = Calendar.getInstance();
            calCur.setTime(calStart.getTime());
            Double thisres;
            while (!calCur.after(calEnd)){
//                System.out.println("cur day:"+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(calCur.getTime()));
//                System.out.println("date of start:"+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(calStart.getTime()));
//                System.out.println("date of end:"+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(calEnd.getTime()));
                if (isSameDay(calCur,calStart)) {//first day, add time from start time to end of day
                    endOfDay.set(calStart.get(Calendar.YEAR),calStart.get(Calendar.MONTH),calStart.get(Calendar.DAY_OF_MONTH),laborTimeEndHours,laborTimeEndMins);
//                    System.out.println("end of day:"+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(endOfDay.getTime()));
                    if (!endOfDay.getTime().before(startDate)) {//if start time after end of day - do nothing
                        thisdaydiff = Double.valueOf(endOfDay.getTime().getTime() - calStart.getTime().getTime());
                        if (thisdaydiff < 0) thisdaydiff = thisdaydiff * -1;
                        thisres = Double.valueOf(thisdaydiff / 1000 / 60 / 60);
                        thisres = Math.round(thisres * 100.d) / 100.0d;
                        res += thisres;
                    }
                }else if (isSameDay(calCur,calEnd)){// last day, add time from start of business time to end of day
                    Calendar begOfDay = Calendar.getInstance();
                    begOfDay.set(calEnd.get(Calendar.YEAR),calEnd.get(Calendar.MONTH),calEnd.get(Calendar.DAY_OF_MONTH),laborTimeStartHours,laborTimeStartMins);
                    if (begOfDay.before(calEnd)) {//if date of ending is before labour hours - do nothing
                        thisdaydiff = Double.valueOf(endDate.getTime() - begOfDay.getTime().getTime());
                        if (thisdaydiff < 0) thisdaydiff = thisdaydiff * -1;
                        thisres = Double.valueOf(thisdaydiff / 1000 / 60 / 60);
                        thisres = Math.round(thisres * 100.d) / 100.0d;
                        res += thisres;
                    }
                }else{
                    if (!(calCur.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || calCur.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY))
                        res += 8.0; //add full working day, omit weekends
                }
                calCur.add(Calendar.DAY_OF_MONTH,1);
                calCur.set(calCur.get(Calendar.YEAR),calCur.get(Calendar.MONTH),calCur.get(Calendar.DAY_OF_MONTH),laborTimeStartHours,laborTimeStartMins);
            }
        }
        return res;
    }

    //  ======================  F I R S T    R E P O R T   ==============================

    /**
     * @param resSet   - result of query: type of application (GENERIC,GENERIC WITHOUT BO,CANCELLATION,PIP,PO and so on
     *                 date of event(changing of application status), event type (RegState), average duration of
     *                 workflow stage, number of application, that have appropriated status in given data range
     * @param row      - row of table
     * @param datatype - which data should be placed amount of apps OR average
     * @return updated row of table
     */

    private AppOverall fillRowForReport(List<Object[]> resSet, AppOverall row, String datatype) {
        if (resSet != null && resSet.size() > 0) {
            for (Object[] params : resSet) {
                Object[] obj = params;
                String kpiName = (String) obj[0];
                Double kpiValue = 0.0;
                if ("count".equalsIgnoreCase(datatype)) {
                    BigInteger value = (BigInteger) obj[4];
                    kpiValue = value.doubleValue();
                } else {
                    BigDecimal value = ((BigDecimal) obj[1]);
                    kpiValue = value.doubleValue();
                    if (kpiValue < 0) kpiValue = Math.abs(kpiValue);
                }


                if (kpiName.equalsIgnoreCase(ProdAppType.GENERIC.toString())) {
                    row.setRegGBE(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.GENERIC_NO_BE.toString())) {
                    row.setRegGWoBE(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.NEW_CHEMICAL_ENTITY.toString())) {
                    row.setRegNewMol(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.RENEW.toString())) {
                    row.setRenew(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.VARIATION.toString())) {
                    row.setVariation(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase("PIP")) {
                    row.setPip(kpiValue);
                } else if (kpiName.equalsIgnoreCase("PO")) {
                    row.setPo(kpiValue);
                } else if (kpiName.equalsIgnoreCase("SC")) {
                    row.setSuspCanc(kpiValue);
                }
            }
        }
        return row;
    }

    private AppOverallReg fillRowForRegistartion(List<Object[]> resSet, AppOverallReg row, String valueType) {
        if (resSet != null && resSet.size() > 0) {
            for (Object[] obj : resSet) {
                String kpiName = (String) obj[0];
                Double kpiValue = 0.0;
                if ("time".equals(valueType)) {
                    BigDecimal value = (BigDecimal) obj[1];
                    kpiValue = value.doubleValue();
                }else{
                    BigInteger value = (BigInteger) obj[6];
                    if (value!=null) kpiValue = value.doubleValue();
                }
                if (kpiName.startsWith(ProdAppType.GENERIC.toString())) {
                    if (kpiName.endsWith("SRA")) {
                        row.setRegGBESRA(Double.valueOf(kpiValue));
                    }else if (kpiName.endsWith("FT")) {
                        row.setRegGBEFast(Double.valueOf(kpiValue));
                    }else {
                        row.setRegGBE(Double.valueOf(kpiValue));
                    }
                } else if (kpiName.startsWith(ProdAppType.GENERIC_NO_BE.toString())) {
                    if (kpiName.endsWith("SRA")) {
                        row.setRegGWoBESRA(Double.valueOf(kpiValue));
                    }else if (kpiName.endsWith("FT")){
                        row.setRegGWoBEFast(Double.valueOf(kpiValue));
                    }else {
                        row.setRegGWoBE(Double.valueOf(kpiValue));
                    }
                } else if (kpiName.startsWith(ProdAppType.NEW_CHEMICAL_ENTITY.toString())) {
                    if (kpiName.endsWith("SRA")) {
                        row.setRegNewMolSRA(Double.valueOf(kpiValue));
                    }else if (kpiName.endsWith("FT")) {
                        row.setRegNewMolFast(Double.valueOf(kpiValue));
                    }else {
                        row.setRegNewMol(Double.valueOf(kpiValue));
                    }
                } else if (kpiName.equalsIgnoreCase(ProdAppType.RENEW.toString())) {
                    row.setRenew(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.VARIATION.toString())) {
                    row.setVariation(Double.valueOf(kpiValue));
                }else if (kpiName.equalsIgnoreCase("SUSPENSION")) {
                    row.setSuspCanc(Double.valueOf(kpiValue));
                }
            }
        }
        return row;
    }


    private AppOverall fillRowForCSO(List<Object[]> resSet, AppOverall row) {
        if (resSet != null && resSet.size() > 0) {
            for (Object[] obj : resSet) {
                String kpiName = (String) obj[1];
                BigInteger value = (BigInteger) obj[5];
                Double kpiValue = 0.0;
                if (value != null)
                    kpiValue = value.doubleValue();
                if (kpiName.equalsIgnoreCase(ProdAppType.GENERIC.toString())) {
                    row.setRegGBE(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.GENERIC_NO_BE.toString())) {
                    row.setRegGWoBE(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.NEW_CHEMICAL_ENTITY.toString())) {
                    row.setRegNewMol(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.RENEW.toString())) {
                    row.setRenew(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase(ProdAppType.VARIATION.toString())) {
                    row.setVariation(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase("PIP")){
                    row.setPip(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase("PO")){
                    row.setPo(Double.valueOf(kpiValue));
                } else if (kpiName.equalsIgnoreCase("SC")){
                    row.setSuspCanc(Double.valueOf(kpiValue));
                }
            }
        }
        return row;
    }

    private AppOverall addValueToRow(Object[] record, AppOverall row) {
        String appType = (String) record[1];
        Double existingValue = 0.0;
        if ("PIP".equals(appType)) {
            existingValue = row.getPip();
            row.setPip(existingValue + 1);
        } else if ("PO".equals(appType)) {
            existingValue = row.getPo();
            row.setPo(existingValue + 1);
        } else if ("SC".equals(appType)) {
            existingValue = row.getSuspCanc();
            row.setPip(existingValue + 1);
        } else if (ProdAppType.GENERIC.toString().equalsIgnoreCase(appType)) {
            existingValue = row.getRegGBE();
            row.setRegGBE(existingValue + 1);
        } else if (ProdAppType.GENERIC_NO_BE.toString().equalsIgnoreCase(appType)) {
            existingValue = row.getRegGWoBE();
            row.setRegGWoBE(existingValue + 1);
        } else if (ProdAppType.NEW_CHEMICAL_ENTITY.toString().equalsIgnoreCase(appType)) {
            existingValue = row.getRegNewMol();
            row.setRegNewMol(existingValue + 1);
        } else if (ProdAppType.RENEW.toString().equalsIgnoreCase(appType)) {
            existingValue = row.getRenew();
            row.setRenew(existingValue + 1);
        } else if (ProdAppType.VARIATION.toString().equalsIgnoreCase(appType)) {
            existingValue = row.getVariation();
            row.setVariation(existingValue + 1);
        }

        row.getTotal();
        return row;
    }

    private AppOverall calcApplicationReturnedFromFeedback(List<Object[]> resSet, AppOverall row) {
        if (resSet.size() > 0) {
            for (int i = 0; i < resSet.size(); i++) {
                //get next follow_up event (send to feedback)
                Object[] timeLineRecord = resSet.get(i);
                Long tl_id = ((BigInteger) timeLineRecord[7]).longValue();
                Long app_id = ((BigInteger) timeLineRecord[0]).longValue();
                String dtype = (String) timeLineRecord[1];
                //find all events for the application
                List<Object[]> found = customTimelineDAO.findAllEventsByAppId(app_id, dtype);
                //search current FOLLOW_UP timeline in all timelines for current application
                for (int j = 0; j < found.size(); j++) {
                    Object[] follow_up_timeline = found.get(j);
                    Long curId = ((BigInteger) follow_up_timeline[0]).longValue();
                    if (curId.equals(tl_id)) {
                        if (j < found.size() - 1) {//i.e. foolow_up event is not last
                            Object[] next_timeLine = found.get(j + 1);
                            String nextState = (String) next_timeLine[2];
                            if (nextState.equalsIgnoreCase(RegState.NEW_APPL.toString()) || nextState.equalsIgnoreCase(RegState.REVIEW_BOARD.toString())
                                    || nextState.equalsIgnoreCase(RegState.RECOMMENDED.toString()) || nextState.equalsIgnoreCase(RegState.NOT_RECOMMENDED.toString())) {
                                row = addValueToRow(timeLineRecord, row);
                                break;
                            }
                        }
                    }
                }

            }
        }
        return row;
    }

    public List<AppOverall> createCSDreport1(Date startDate, Date endDate) {
        customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.REGISTERED);
        List<AppOverall> res = new ArrayList<AppOverall>();
        AppOverall row = new AppOverall();
        row.setKpiName("Number of  Applications  Received");
        List<Object[]> resSet;
        resSet = customTimelineDAO.findAllApplicationsByType(startDate, endDate, RegState.NEW_APPL);
        fillRowForCSO(resSet, row);
        res.add(row);

        //second row - prescreening
        resSet = customTimelineDAO.findAllApplicationsByType(startDate, endDate, RegState.SCREENING);
        row = new AppOverall();
        row.setKpiName("Number of Application Pre-screened");
        fillRowForCSO(resSet, row);
        res.add(row);
        //for suspension/cancellation prescreening is empty

        //THIRD row
        //search timelines FOLLOW_UP events (i.e. return TO applicant's feedback)
        resSet = customTimelineDAO.findAllApplicationsByType(startDate, endDate, RegState.FOLLOW_UP);
        row = new AppOverall();
        row.setKpiName("Number of Applications Returned with Feedback");
        fillRowForCSO(resSet, row);
        res.add(row);

        //FOURTH ROW - submitted to approvement
        row = new AppOverall();
        row.setKpiName("Number of Applications submitted for approving");
        resSet = customTimelineDAO.findEventsByType(startDate, endDate, RegState.FOLLOW_UP, null);
        row = calcApplicationReturnedFromFeedback(resSet, row);
        res.add(row);

        //FIFTH ROW
        row = new AppOverall();
        row.setKpiName("Number of Applications Verified/Approved");
        resSet = customTimelineDAO.findAllApplicationsByType(startDate, endDate, RegState.VERIFY);
        row = fillRowForCSO(resSet,row);
        resSet = customTimelineDAO.findAllApplicationsByType(startDate, endDate, RegState.REGISTERED);
        AppOverall add = new AppOverall();
        add = fillRowForCSO(resSet, add);
        // only registered PIP & PO should be added to this row
        row.setPip(row.getPip()+add.getPip());
        row.setPip(row.getPo()+add.getPo());
        res.add(row);

        //SIXTH ROW (Approving time)
        row = new AppOverall();
        row.setKpiName("Average Processing Time for Verified/Approved applications");
        resSet = customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.REGISTERED);
        row = fillRowForReport(resSet, row, "avg time");
        res.add(row);

        //SEVENTH ROW
        row = new AppOverall();
        row.setKpiName("Number of Applications Rejected");
        resSet = customTimelineDAO.findAllApplicationsByType(startDate, endDate, RegState.REJECTED);
        row = fillRowForCSO(resSet, row);

        res.add(row);

        //EIGHT ROW (Approving time)
        row = new AppOverall();
        row.setKpiName("Average Processing Time for rejected applications");
        resSet = customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.REJECTED);
        row = fillRowForReport(resSet, row, "avg time");
        res.add(row);

        return res;
    }

    /*
    public List<AppOverall> createRegReport1(Date startDate, Date endDate) {
        List<AppOverall> res = new ArrayList<AppOverall>();
        AppOverall row = new AppOverall();
        row.setKpiName("Number of  Verfied Applications  Received in the reporting period");
        List<Object[]> resSet;
        Long employeeId = Long.valueOf(0);
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate, endDate, RegState.VERIFY,employeeId);
        row = fillRowForReport(resSet, row, "count");
        res.add(row);

        row = new AppOverall();
        row.setKpiName("Number of Applications for which Further Information Requested");
        resSet = customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.FOLLOW_UP);
        row = fillRowForReport(resSet, row, "count");
        res.add(row);

        //(Approved application)
        row = new AppOverall();
        row.setKpiName("Number of Applications Approved");
        resSet = customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.REGISTERED);
        row = fillRowForReport(resSet, row, "count");
        res.add(row);

        //(Approving time)
        row = new AppOverall();
        row.setKpiName("Average Processing Time for Approval in Hours in MRIS");
        resSet = customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.REGISTERED);
        row = fillRowForReport(resSet, row, "avg time");
        res.add(row);

        //(Rejecting applications)
        row = new AppOverall();
        row.setKpiName("Number of Applications Rejected");
        resSet = customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.REJECTED);
        row = fillRowForReport(resSet, row, "count");
        res.add(row);

        //(Rejecting time)
        row = new AppOverall();
        row.setKpiName("Average Processing Time for Verified/Approved applications");
        resSet = customTimelineDAO.calculateApplicationsProcesingTime(startDate, endDate, RegState.REJECTED);
        row = fillRowForReport(resSet, row, "avg time");
        res.add(row);

        return res;
    }
    */

    public List<AppOverallReg> createRegReport12(Date startDate, Date endDate, Long employeeId) {
        List<AppOverallReg> res = new ArrayList<AppOverallReg>();

        List<Object[]> resSet;
        AppOverallReg row = new AppOverallReg();
        row.setKpiName("Number of  Verfied Applications  Received in the reporting period");
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate,endDate,RegState.VERIFY,employeeId);
        row = fillRowForRegistartion(resSet,row,"count");
        res.add(row);

        row = new AppOverallReg();
        row.setKpiName("Number of Applications for which Further Information Requested");
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate,endDate,RegState.FOLLOW_UP,employeeId);
        row = fillRowForRegistartion(resSet,row,"count");
        res.add(row);

        row = new AppOverallReg();
        row.setKpiName("Number of Applications under Review");
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate,endDate,RegState.REVIEW_BOARD,employeeId);
        row = fillRowForRegistartion(resSet,row,"count");
        res.add(row);

        row = new AppOverallReg();
        row.setKpiName("Average Processing Time for Approval in hours");
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate,endDate,RegState.REGISTERED,employeeId);
        row = fillRowForRegistartion(resSet,row,"time");
        res.add(row);

        row = new AppOverallReg();
        row.setKpiName("Number of Applications Approved");
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate,endDate,RegState.REGISTERED,employeeId);
        row = fillRowForRegistartion(resSet,row,"count");
        res.add(row);

        row = new AppOverallReg();
        row.setKpiName("Average Processing Time for Rejection in hours");
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate,endDate,RegState.REJECTED,employeeId);
        row = fillRowForRegistartion(resSet,row,"time");
        res.add(row);

        row = new AppOverallReg();
        row.setKpiName("Number of Applications Rejected");
        resSet = customTimelineDAO.calculateApplicationsRegProcesingTimeByEmployee(startDate,endDate,RegState.REJECTED,employeeId);
        row = fillRowForRegistartion(resSet,row,"count");
        res.add(row);

        return res;
    }


    public List<AppOverall> createCSDReport2(Date startDate, Date endDate, Long employeeId) {
        List<AppOverall> res = new ArrayList<AppOverall>();
        AppOverall row = new AppOverall();

        List<Object[]> resSet;
        resSet = customTimelineDAO.calculateApplicationsProcesingTimeByEmployee(startDate, endDate, RegState.NEW_APPL, null);
        row = fillRowForReport(resSet, row, "count");
        row.setKpiName("Number of  Applications  Received");
        res.add(row);

        resSet = customTimelineDAO.calculateApplicationsProcesingTimeByEmployee(startDate, endDate, RegState.SCREENING, employeeId);
        row = new AppOverall();
        row.setKpiName("Number of Application Pre-screened");
        row = fillRowForReport(resSet, row, "count");
        res.add(row);

        row = new AppOverall();
        row.setKpiName("Number of Applications Returned with Feedback");
        resSet = customTimelineDAO.findEventsByType(startDate, endDate, RegState.FOLLOW_UP, employeeId);
        row = calcApplicationReturnedFromFeedback(resSet, row);
        res.add(row);

        resSet = customTimelineDAO.countApplicationProcesedByTheCSO(startDate, endDate, RegState.REGISTERED, employeeId);
        row = new AppOverall();
        row.setKpiName("Number of Applications Verified/Approved");
        row = fillRowForReport(resSet, row, "count");
        res.add(row);

        resSet = customTimelineDAO.countApplicationProcesedByTheCSO(startDate, endDate, RegState.REJECTED, employeeId);
        row = new AppOverall();
        row.setKpiName("Number of Applications rejected");
        row = fillRowForReport(resSet, row, "count");
        res.add(row);

        return res;
    }

}
