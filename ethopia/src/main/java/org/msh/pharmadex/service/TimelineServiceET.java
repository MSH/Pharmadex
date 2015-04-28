package org.msh.pharmadex.service;

import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.util.RetObject;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by usrivastava on 01/24/2015.
 */
@Service
public class TimelineServiceET extends TimelineService implements Serializable {

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

    public RetObject validatescreening(List<ProdAppChecklist> prodAppChecklists) {
        RetObject retObject = new RetObject();
        for(ProdAppChecklist prodAppChecklist : prodAppChecklists){
            if(prodAppChecklist.getChecklist().isHeader()) {
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
