package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.TimelineDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.YesNoNA;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class TimelineService implements Serializable {

    private static final long serialVersionUID = 7475406605652667214L;

    @Autowired
    TimelineDAO timelineDAO;

    @Autowired
    WorkspaceDAO workspaceDAO;

    @Autowired
    ReviewService reviewService;

    List<TimeLine> timeLineList;

    @Autowired
    ProdApplicationsService prodApplicationsService;

    public List<TimeLine> findTimelineByApp(Long prodApplications_Id) {
        timeLineList = timelineDAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
        if(timeLineList != null && timeLineList.size() > 0)
	        Collections.sort(timeLineList, new Comparator<TimeLine>() {
				@Override
				public int compare(TimeLine o1, TimeLine o2) {
					Long id1 = o1.getId();
					Long id2 = o2.getId();
					return -id1.compareTo(id2);
				}
			});
        return timeLineList;
    }

    @Transactional
    public RetObject saveTimeLine(TimeLine timeLine) {
        RetObject retObject = new RetObject();
        String msg = validateStatusChange(timeLine);
        TimeLine timeline;
        if (msg.equals("success")) {
            timeline = timelineDAO.saveAndFlush(timeLine);
            retObject = prodApplicationsService.updateProdApp(timeline.getProdApplications(), timeline.getUser().getUserId());
            timeline.setProdApplications((ProdApplications) retObject.getObj());
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
            if(prodApplications.getModerator()==null)
                return "valid_assign_moderator";
        } else if (timeLine.getRegState().equals(RegState.REVIEW_BOARD) || timeLine.getRegState().equals(RegState.REGISTERED)) {
            if (workspaceDAO.findAll().get(0).isDetailReview()) {
                List<ReviewInfo> reviewInfos = reviewService.findReviewInfos(prodApplications.getId());
                if (reviewInfos == null || reviewInfos.size() == 0)
                    return "valid_assign_reviewer";
            } else {
                List<Review> reviews = reviewService.findReviews(prodApplications.getId());
                if (reviews.size() == 0)
                    return "valid_assign_reviewer";
            }
        }
        return "success";

    }

    public RetObject validatescreening(List<ProdAppChecklist> prodAppChecklists) {
        RetObject retObject = new RetObject();
        for (ProdAppChecklist prodAppChecklist : prodAppChecklists) {
            if (prodAppChecklist.getChecklist().isHeader()) {
                if (prodAppChecklist.getValue().equals(YesNoNA.YES)) {
                    if (prodAppChecklist.getStaffValue().equals(YesNoNA.NO)) {
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
