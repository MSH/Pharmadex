package org.msh.pharmadex.mbean.product;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;
import org.msh.pharmadex.service.ReviewService;
import org.msh.pharmadex.service.ReviewServiceMZ;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RegistrationUtil;
import org.msh.pharmadex.util.RetObject;

import net.sf.jasperreports.engine.JRException;

/**
 * Backing bean to process the application made for registration
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnMZ implements Serializable {

	private static final long serialVersionUID = 3698548480414883122L;

	protected FacesContext facesContext = getCurrentInstance();
	protected java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

	@ManagedProperty(value = "#{prodApplicationsServiceMZ}")
	protected ProdApplicationsServiceMZ prodApplicationsServiceMZ;

	@ManagedProperty(value = "#{prodApplicationsService}")
	protected ProdApplicationsService prodApplicationsService;

	@ManagedProperty(value = "#{reviewService}")
	protected ReviewService reviewService;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;

	@ManagedProperty(value = "#{processProdBn}")
	private ProcessProdBn processProdBn;

	public User loggedInUser;
	private String gestorDeCTRM = resourceBundle.getString("gestorDeCTRM_value");

	private boolean visibleExecSumeryBtn = false;
	private boolean disableCheckSample = false;

	@PostConstruct
	private void init() {
		try {
			//facesContext = FacesContext.getCurrentInstance();

			loggedInUser = userService.findUser(userSession.getLoggedINUserID());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<RegState> getRegSate() {
		if (getProcessProdBn().getProdApplications() != null)
			return prodApplicationsServiceMZ.nextStepOptions();
		return null;
	}

	public void addTimeline() {
		facesContext = getCurrentInstance();
		try {
			getProcessProdBn().getTimeLine().setStatusDate(new Date());
			getProcessProdBn().getTimeLine().setUser(loggedInUser);
			RetObject paObject = getProcessProdBn().getProdApplicationsService().updateProdApp(getProcessProdBn().getProdApplications(), loggedInUser.getUserId());
			getProcessProdBn().setProdApplications((ProdApplications) paObject.getObj());
			getProcessProdBn().getTimeLine().setProdApplications(getProcessProdBn().getProdApplications());

			//String retValue = getProcessProdBn().getTimelineService().validateStatusChange(getProcessProdBn().getTimeLine());

			//if (retValue.equalsIgnoreCase("success")) {
			getProcessProdBn().getProdApplications().setRegState(getProcessProdBn().getTimeLine().getRegState());
			RetObject retObject = getProcessProdBn().getProdApplicationsService().updateProdApp(getProcessProdBn().getProdApplications(), loggedInUser.getUserId());
			if (retObject.getMsg().equals("persist")) {
				getProcessProdBn().setProdApplications((ProdApplications) retObject.getObj());
				//changeStateReviewInfo();
				getProcessProdBn().setFieldValues();
				getProcessProdBn().getTimeLine().setProdApplications(getProcessProdBn().getProdApplications());
				getProcessProdBn().getTimelineService().saveTimeLine(getProcessProdBn().getTimeLine());
				getProcessProdBn().getTimeLineList().add(getProcessProdBn().getTimeLine());
				facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("status_change_success")));
			} else {
				facesContext.addMessage(null, new FacesMessage(retObject.getMsg()));
			}
			/*} else if (retValue.equalsIgnoreCase("fee_not_recieved")) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
			} else if (retValue.equalsIgnoreCase("app_not_verified")) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("fee_not_recieved")));
			} else if (retValue.equalsIgnoreCase("prod_not_verified")) {
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), resourceBundle.getString("prod_not_verified")));
			} else if (retValue.equalsIgnoreCase("valid_assign_moderator")) {
				facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_moderator")));
			} else if (retValue.equalsIgnoreCase("valid_assign_reviewer")) {
				facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("valid_assign_reviewer")));
			}*/

		} catch (Exception e) {
			e.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("global_fail")));
		}
		getProcessProdBn().setTimeLine(new TimeLine());
		//return "/internal/processprodlist";  //To change body of created methods use File | Settings | File Templates.
	}

	public String nextStep(){
		ProdApplications prodApp = getProcessProdBn().getProdApplications();
		if(prodApp != null){
			RegState curRegState = prodApp.getRegState();
			switch (curRegState) {
			case SCREENING:
				backToSCREENING(prodApp);
				break;
			case REVIEW_BOARD:
				prodApplicationsServiceMZ.changeStateReviewInfo(getProcessProdBn().getProdApplications().getId());
				break;
			}

			addTimeline();
		}
		return "/internal/processprodlist";
	}

	private void backToSCREENING(ProdApplications prodApp){
		prodApp.setModerator(null);
		prodApplicationsService.updateProdApp(prodApp, userSession.getLoggedINUserID());
	}

	public boolean getCanChangeModerator() {
		RegState curRegState = getProcessProdBn().getProdApplications().getRegState();
		if(userSession.isAdmin() && !curRegState.equals(RegState.REGISTERED)
				&& !curRegState.equals(RegState.REJECTED))
			return true;

		if(userSession.isStaff() && !curRegState.equals(RegState.REGISTERED)
				&& !curRegState.equals(RegState.REJECTED) && getProcessProdBn().getProdApplications().getModerator() != null){
			return true;
		}
		return false;
	}

	public boolean getCanNextStep() {
		RegState curRegState = getProcessProdBn().getProdApplications().getRegState();

		if((userSession.isAdmin() || userSession.isModerator()) 
				&& !curRegState.equals(RegState.REGISTERED) && !curRegState.equals(RegState.REJECTED)
				&& !curRegState.equals(RegState.FOLLOW_UP))
			return true;
		return false;
	}

	public List<ProdAppLetter> getLetters() {
		return processProdBn.getLetters();
	}

	public String registerProduct(ProdApplications prodApplications) {
		facesContext = getCurrentInstance();
		try {
			if(prodApplications.getRegState().equals(RegState.REGISTERED))
				prodApplicationsServiceMZ.createRegCert(prodApplications, getGestorDeCTRM());
			else{
				if (!prodApplications.getRegState().equals(RegState.RECOMMENDED)) {
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("register_fail")));
					return "";
				}

				if(prodApplications.getProdRegNo() == null || prodApplications.getProdRegNo().equals(""))
					prodApplications.setProdRegNo(RegistrationUtil.generateRegNo("" + 0, prodApplications.getProdAppNo()));

				prodApplications.setActive(true);
				prodApplications.setUpdatedBy(loggedInUser);

				String retValue = prodApplicationsServiceMZ.registerProd(prodApplications);
				if(retValue.equals("created")) {
					prodApplicationsServiceMZ.createRegCert(prodApplications, getGestorDeCTRM());
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("status_change_success")));
				}else{
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Error registering the product"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Product registered but error generating certificate."));
		} catch (JRException e) {
			e.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Product registered but error generating certificate."));
		} catch (SQLException e) {
			e.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Product registered but error generating certificate."));
		} catch (Exception ex){
			ex.printStackTrace();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Error registering the product"));
		}
		return null;
	}

	public boolean isVisibleExecSumeryBtn() {
		// (userSession.moderator||userSession.admin||userSession.head)and userAccessMBean.detailReview
		if((userSession.isModerator() || userSession.isAdmin() || userSession.isHead()) 
				&& processProdBn.getUserAccessMBean().isDetailReview()){
			// if All ReviewInfo in state ACCEPTED
			List<ReviewInfo> list = processProdBn.getReviewInfos();
			if(list != null && list.size() > 0){
				for(ReviewInfo rinfo:list){
					if(!rinfo.getReviewStatus().equals(ReviewStatus.ACCEPTED)){
						visibleExecSumeryBtn = false;
						return visibleExecSumeryBtn;
					}
				}
				visibleExecSumeryBtn = true;
			}
		}
		return visibleExecSumeryBtn;
	}

	public String executiveSummary(){
		String result = prodApplicationsServiceMZ.verificationBeforeComplete(processProdBn.getProdApplications(), userSession.getLoggedINUserID(), processProdBn.getReviewInfos());
		if (result.equals("ok")) {
			return "/internal/execsumm.faces";
		} else if (result.equals("state_error")) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please accept the reviews before submitting the executive summary", ""));
			return null;
		} else if (result.equals("clinical_review")) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Clinical review not received or verified.", ""));
			return null;
		} else if (result.equals("lab_status")) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Lab result not verified.", ""));
			return null;
		}
		return "";
	}

	public String deleteLetter(ProdAppLetter let) {
		getProcessProdBn().getLetters().remove(let);
		facesContext = FacesContext.getCurrentInstance();
		try {
			prodApplicationsServiceMZ.deleteProdAppLetter(let);
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("del_letter_success")));
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("global_fail"), resourceBundle.getString("del_letter_success")));
			e.printStackTrace();
		}
		return null;
	}

	public void setVisibleExecSumeryBtn(boolean visibleExecSumeryBtn) {
		this.visibleExecSumeryBtn = visibleExecSumeryBtn;
	}

	public ProdApplications findProdApplications() {
		return processProdBn.getProdApplications();
	}

	public ProdApplicationsServiceMZ getProdApplicationsServiceMZ(){
		return prodApplicationsServiceMZ;
	}

	public void setProdApplicationsServiceMZ(ProdApplicationsServiceMZ service){
		this.prodApplicationsServiceMZ = service;
	}

	public ProdApplicationsService getProdApplicationsService(){
		return prodApplicationsService;
	}

	public void setProdApplicationsService(ProdApplicationsService service){
		this.prodApplicationsService = service;
	}

	public ReviewService getReviewService() {
		return reviewService;
	}

	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ProcessProdBn getProcessProdBn() {
		return processProdBn;
	}

	public void setProcessProdBn(ProcessProdBn process) {
		this.processProdBn = process;
	}

	public String getGestorDeCTRM() {
		return gestorDeCTRM;
	}

	public void setGestorDeCTRM(String gestorDeCTRM) {
		this.gestorDeCTRM = gestorDeCTRM;
	}

	public boolean isDisableCheckSample() {
		disableCheckSample = false;
		if(processProdBn.getProdApplications() != null)
			if(processProdBn.getProdApplications().getRegState().ordinal() > 6)
				disableCheckSample = true;
		return disableCheckSample;
	}

	public void setDisableCheckSample(boolean checkSample) {
		this.disableCheckSample = checkSample;
	}
}
