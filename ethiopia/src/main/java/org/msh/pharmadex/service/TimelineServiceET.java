package org.msh.pharmadex.service;

import org.msh.pharmadex.util.StrTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.msh.pharmadex.dao.iface.TimelinePIPDAO;
import org.msh.pharmadex.dao.iface.TimelinePODAO;
import org.msh.pharmadex.dao.iface.TimelineSCDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by usrivastava on 01/24/2015.
 * Updated Odissey 07/12/16
 */
@Service
public class TimelineServiceET extends TimelineService implements Serializable {

    @Autowired
    TimelinePIPDAO timelinePIPDAO;
    @Autowired
    TimelinePODAO timelinePODAO;
    @Autowired
    TimelineSCDAO timelineSCDAO;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProdApplicationsService prodApplicationsService;

    List<TimeLine> timeLineList;

    @Override
    public String validateStatusChange(TimeLine timeLine) {
        ProdApplications prodApplications = timeLine.getProdApplications();
        if (timeLine.getRegState().equals(RegState.FEE) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            if (!prodApplications.isFeeReceived()) {
                timeLine = new TimeLine();
                return "fee_not_recieved";
            }
        } else if (timeLine.getRegState().equals(RegState.VERIFY) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            if (!prodApplications.isApplicantVerified()) {
                timeLine = new TimeLine();
                return "app_not_verified";
            } else if (!prodApplications.isProductVerified() || prodApplications.getRegState() == RegState.REGISTERED) {
                timeLine = new TimeLine();
                return "prod_not_verified";
            }
        } else if (timeLine.getRegState().equals(RegState.SCREENING) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            ProdApplications prodApp = prodApplicationsService.findProdApplications(prodApplications.getId());
            prodApplications.setModerator(prodApp.getModerator());
            if (prodApplications.getModerator() == null)
                return "valid_assign_moderator";
        } else if (timeLine.getRegState().equals(RegState.REVIEW_BOARD) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            if (reviewService.findReviews(prodApplications.getId()).size() == 0)
                return "valid_assign_reviewer";
        }
        return "success";

    }

    public List<TimeLineBase> findTimelineByAppNo(Long prodApplications_Id, POrderBase order) {
        List<TimeLineBase> timeLineEvents = null;
        if (order instanceof PIPOrder) {
            timeLineEvents = timelinePIPDAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
        } else if (order instanceof PurOrder) {
            timeLineEvents = timelinePODAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
        }

        return timeLineEvents;
    }

    public void createTimeLineEvent(POrderBase order, RegState state, User curUser, String comment) {
        if (order instanceof PIPOrder) {
            TimeLinePIP tl = new TimeLinePIP();
            tl.setProdApplications((PIPOrder) order);
            tl.setRegState(state);
            tl.setComment(comment);
            tl.setStatusDate(new Date());
            tl.setUser(curUser);
            timelinePIPDAO.saveAndFlush(tl);
        } else if (order instanceof PurOrder) {
            TimeLinePO tl = new TimeLinePO();
            tl.setProdApplications((PurOrder) order);
            tl.setRegState(state);
            tl.setComment(comment);
            tl.setStatusDate(new Date());
            tl.setUser(curUser);
            timelinePODAO.saveAndFlush(tl);
        }
    }


    public void createTimeLineEvent(SuspDetail order, RegState state, User curUser, String comment) {
            TimeLineSC tl = new TimeLineSC();
            tl.setProdApplications(order);
            tl.setRegState(state);
            tl.setComment(comment);
            tl.setStatusDate(new Date());
            tl.setUser(curUser);
            timelineSCDAO.saveAndFlush(tl);
    }

    public TimeLine createTimeLineEvent(ProdApplications prodApplications, RegState state, User curUser, String comment) {
        TimeLine tl = new TimeLine();
        tl.setProdApplications(prodApplications);
        tl.setRegState(state);
        tl.setComment(comment);
        tl.setStatusDate(new Date());
        tl.setUser(curUser);
        timelineDAO.saveAndFlush(tl);
        return tl;
    }

    public TimelinePIPDAO getTimelinePIPDAO() {
        return timelinePIPDAO;
    }

    public void setTimelinePIPDAO(TimelinePIPDAO timelinePIPDAO) {
        this.timelinePIPDAO = timelinePIPDAO;
    }

    public TimelinePODAO getTimelinePODAO() {
        return timelinePODAO;
    }

    public void setTimelinePODAO(TimelinePODAO timelinePODAO) {
        this.timelinePODAO = timelinePODAO;
    }

    public TimelineSCDAO getTimelineSCDAO() {
        return timelineSCDAO;
    }

    public void setTimelineSCDAO(TimelineSCDAO timelineSCDAO) {
        this.timelineSCDAO = timelineSCDAO;
    }
}
