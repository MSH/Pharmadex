package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.TimeLinePIPDAO;
import org.msh.pharmadex.dao.iface.TimeLinePODAO;
import org.msh.pharmadex.dao.iface.TimeLineSCDAO;
import org.msh.pharmadex.dao.iface.TimelineDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.util.RetObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by usrivastava on 01/24/2015.
 * Updated Odissey 07/12/16
 */
@Service
public class TimelineServiceET extends TimelineService implements Serializable {

    @ManagedProperty(value = "#{timeLineDAO}")
    TimeLinePIPDAO timeLinePIPDAO;

    @ManagedProperty(value = "#{timeLineDAO}")
    TimeLinePODAO timeLinePODAO;

    @ManagedProperty(value = "#{timeLineDAO}")
    TimeLineSCDAO timeLineSCDAO;


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
            if(prodApplications.getModerator()==null)
                return "valid_assign_moderator";
        } else if (timeLine.getRegState().equals(RegState.REVIEW_BOARD) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            if(reviewService.findReviews(prodApplications.getId()).size()==0)
                return "valid_assign_reviewer";
        }
        return "success";

    }

    public List<TimeLineBase> findTimelineByAppNo(Long prodApplications_Id, POrderBase order) {
        List<TimeLineBase> timeLineEvents = null;
        if (order instanceof  PIPOrder) {
            timeLineEvents = timeLinePIPDAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
        }else if (order instanceof PurOrder){
            timeLineEvents = timeLinePODAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
        }
        if(timeLineList != null && timeLineList.size() > 0)
            Collections.sort(timeLineList, new Comparator<TimeLineBase>() {

                @Override
                public int compare(TimeLineBase o1, TimeLineBase o2) {
                    Long id1 = o1.getId();
                    Long id2 = o2.getId();
                    return -id1.compareTo(id2);
                }
            });
        return timeLineEvents;
    }

    public TimeLinePIPDAO getTimeLinePIPDAO() {
        return timeLinePIPDAO;
    }

    public void setTimeLinePIPDAO(TimeLinePIPDAO timeLinePIPDAO) {
        this.timeLinePIPDAO = timeLinePIPDAO;
    }

    public TimeLinePODAO getTimeLinePODAO() {
        return timeLinePODAO;
    }

    public void setTimeLinePODAO(TimeLinePODAO timeLinePODAO) {
        this.timeLinePODAO = timeLinePODAO;
    }

    public TimeLineSCDAO getTimeLineSCDAO() {
        return timeLineSCDAO;
    }

    public void setTimeLineSCDAO(TimeLineSCDAO timeLineSCDAO) {
        this.timeLineSCDAO = timeLineSCDAO;
    }
}
