package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;

import org.msh.pharmadex.dao.iface.TimelineDAO;
import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.Review;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.YesNoNA;
import org.msh.pharmadex.util.RetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		/*		if(timeLineList != null && timeLineList.size() > 0)
			Collections.sort(timeLineList, new Comparator<TimeLine>() {
				@Override
				public int compare(TimeLine o1, TimeLine o2) {
					Long id1 = o1.getId();
					Long id2 = o2.getId();
					return -id1.compareTo(id2);
				}
			});*/
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
			addMilestone(timeLine); //at this place related prodapplication has id!

		} else {
			retObject.setMsg(msg);
			retObject.setObj(null);
		}
		return retObject;
	}

	/**
	 * For some timelines we should add extra timeline called milestone
	 * Milestone is unique timeline that we are using to build performance reports
	 * Milestone's logic is very simple, if milestone is not exist, then create it. 
	 * Sometimes we will need to change status date, however we will never add a milestone if a milestone with same state is existed. 
	 * Legacy events like VERIFY, SCREENING etc are triggers 
	 * @param timeline
	 * @return
	 */
	private void addMilestone(TimeLine timeline) {
		//The first VERIFY event always start an application as well as screening
		if(timeline.getRegState().equals(RegState.VERIFY)){
			TimeLine msStart = searchTimelineFor(timeline.getProdApplications().getId(), RegState.MS_START);
			if(msStart == null){
				msStart = createMileStone(RegState.MS_START, timeline);
				msStart = timelineDAO.saveAndFlush(msStart);
				TimeLine msScrStart = createMileStone(RegState.MS_SCR_START, timeline);
				msStart = timelineDAO.saveAndFlush(msScrStart);
			}
		}
		//End of screening is the latest screening date
		if(timeline.getRegState().equals(RegState.SCREENING)){
			TimeLine msScrEnd = searchTimelineFor(timeline.getProdApplications().getId(), RegState.MS_SCR_END);
			if(msScrEnd == null){
				msScrEnd = createMileStone(RegState.MS_SCR_END, timeline);
			}else{
				msScrEnd.setStatusDate(timeline.getStatusDate());
			}
			msScrEnd = timelineDAO.saveAndFlush(msScrEnd);
		}
		//When an application registers or rejects first time it always finishes an evaluation. In addition if screening is not finished yet - finish it!
		if(timeline.getRegState().equals(RegState.REGISTERED) || timeline.getRegState().equals(RegState.REJECTED)){
			TimeLine msEnd = searchTimelineFor(timeline.getProdApplications().getId(), RegState.MS_END);
			if(msEnd == null){
				msEnd = createMileStone(RegState.MS_END, timeline);
				msEnd = timelineDAO.saveAndFlush(msEnd);
			}
			TimeLine msScrEnd = searchTimelineFor(timeline.getProdApplications().getId(), RegState.MS_SCR_END);
			if(msScrEnd == null){
				msScrEnd = createMileStone(RegState.MS_SCR_END, timeline);
				msScrEnd = timelineDAO.saveAndFlush(msScrEnd);
			}
		}
		// Application fee received is definitely start of review
		if(timeline.getRegState().equals(RegState.APPL_FEE)){
			TimeLine msRevStart = searchTimelineFor(timeline.getProdApplications().getId(), RegState.MS_REV_START);
			if(msRevStart == null){
				msRevStart = createMileStone(RegState.MS_REV_START, timeline);
				msRevStart = timelineDAO.saveAndFlush(msRevStart);
			}
		}
		//End of review is the latest executive summary
		if(timeline.getRegState().equals(RegState.RECOMMENDED) || timeline.getRegState().equals(RegState.NOT_RECOMMENDED)){
			TimeLine msRevEnd = searchTimelineFor(timeline.getProdApplications().getId(), RegState.MS_REV_END);
			if(msRevEnd == null){
				msRevEnd = createMileStone(RegState.MS_REV_END, timeline);
			}else{
				msRevEnd.setStatusDate(timeline.getStatusDate());
			}
			msRevEnd = timelineDAO.saveAndFlush(msRevEnd);
		}

	}

	/**
	 * Search for time lime for application with regState given, first or last
	 * @param applicationId
	 * @param regState
	 * @return null, if not found
	 */
	private TimeLine searchTimelineFor(Long applicationId, RegState regState) {
		TimeLine ret = null;
		List<TimeLine> events = findTimelineByApp(applicationId);
		if(events!=null){
			for(TimeLine event : events){
				if(event.getRegState().equals(regState)){
					ret = event;
				}
			}
		}
		return ret;
	}

	/**
	 * Create a milestone with regState and based on timeline given
	 * @param regState
	 * @param timeline
	 * @return
	 */
	private TimeLine createMileStone(RegState regState, TimeLine timeline) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		TimeLine ret = new TimeLine();
		ret.setRegState(regState);
		ret.setStatusDate(timeline.getStatusDate());
		ret.setProdApplications(timeline.getProdApplications());
		ret.setUser(timeline.getUser());
		ret.setComment(resourceBundle.getString(ret.getRegState().getKey()));
		return ret;
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

	public TimeLine createTimeLine(String comm, RegState rstate, ProdApplications prodApp, User user){
		TimeLine tl = new TimeLine();
		tl.setComment(comm);
		tl.setRegState(rstate);
		tl.setProdApplications(prodApp);
		tl.setUser(user);
		tl.setStatusDate(new Date());

		tl = timelineDAO.saveAndFlush(tl);
		prodApplicationsService.updateProdApp(prodApp, tl.getUser().getUserId());
		addMilestone(tl);
		return tl;
	}
}
