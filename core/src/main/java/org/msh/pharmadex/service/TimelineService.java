package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.TimelineDAO;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class TimelineService implements Serializable {

    private static final long serialVersionUID = 7475406605652667214L;

    @Autowired
    TimelineDAO timelineDAO;

    List<TimeLine> timeLineList;

    @Autowired
    ProdApplicationsService prodApplicationsService;

    public List<TimeLine> findTimelineByApp(Long prodApplications_Id) {
        timeLineList = timelineDAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
        return timeLineList;
    }

    public RetObject saveTimeLine(TimeLine timeLine) {
        RetObject retObject = new RetObject();
        String msg = validateStatusChange(timeLine);
        TimeLine timeline;
        if (msg.equals("success")) {
            timeline = timelineDAO.saveAndFlush(timeLine);
            timeline.setProdApplications(prodApplicationsService.updateProdApp(timeline.getProdApplications()));
            retObject.setObj(timeline);
            retObject.setMsg("persist");
        } else {
            retObject.setMsg(msg);
            retObject.setObj(null);
        }
        return retObject;
    }

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
            if(prodApplications.getReviews().size()==0)
                return "valid_assign_reviewer";
        }
        return "success";

    }

    public RetObject validatescreening(List<ProdAppChecklist> prodAppChecklists) {
        RetObject retObject = new RetObject();
        for (ProdAppChecklist prodAppChecklist : prodAppChecklists) {
            if (prodAppChecklist.getChecklist().isHeader()) {
                if (prodAppChecklist.isValue()) {
                    if (!prodAppChecklist.isStaffValue()) {
                        retObject.setMsg("error");
                        return retObject;
                    }
                }
            }
        }
        retObject.setMsg("persist");
        return retObject;
    }

}
