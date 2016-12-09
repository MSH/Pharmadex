package org.msh.pharmadex.mbean.product;

import org.apache.commons.io.IOUtils;
import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdAppChecklistService;
import org.msh.pharmadex.service.ProdApplicationsService;
import org.msh.pharmadex.service.TimelineService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RetObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by utkarsh on 2/23/15.
 */
@ManagedBean
@ViewScoped
public class PreScreenProdMBn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6084133861211974339L;
	@ManagedProperty(value = "#{processProdBn}")
	protected ProcessProdBn processProdBn;
	protected boolean displayScreenAction;
	protected FacesContext facesContext;
	protected ResourceBundle resourceBundle;
	@ManagedProperty(value = "#{prodAppChecklistService}")
	ProdAppChecklistService prodAppChecklistService;
	@ManagedProperty(value = "#{timelineService}")
	private TimelineService timelineService;
	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;
	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	@ManagedProperty(value="#{prodApplicationsService}")
	ProdApplicationsService prodApplicationsService;


	private TimeLine timeLine = new TimeLine();
	private User moderator;
	private List<ProdAppChecklist> prodAppChecklists;
	private ProdAppChecklist prodAppChecklist;
	private UploadedFile file;
	private String fileName = "";

	public String completeScreen() {
		facesContext = FacesContext.getCurrentInstance();
		resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		RetObject retObject = prodAppChecklistService.saveProdAppChecklists(prodAppChecklists);
		prodAppChecklists = (List<ProdAppChecklist>) retObject.getObj();
		if (!retObject.getMsg().equals("persist")) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, retObject.getMsg(), retObject.getMsg()));
			return "";
		}

		ProdApplications prodApplications = processProdBn.getProdApplications();
		prodApplications.setModerator(moderator);
		prodApplicationsService.updateProdApp(prodApplications,userSession.getLoggedINUserID());

		if (prodApplications.getRegState().equals(RegState.NEW_APPL) || prodApplications.getRegState().equals(RegState.FOLLOW_UP)
				|| prodApplications.getRegState().equals(RegState.VERIFY)) {
			timeLine = new TimeLine();
			timeLine.setProdApplications(prodApplications);
			timeLine.setRegState(RegState.SCREENING);
			timeLine.setStatusDate(new Date());
			timeLine.setUser(userService.findUser(userSession.getLoggedINUserID()));
			timeLine.setComment("Pre-Screening completed successfully");
			String ret = timelineService.validateStatusChange(timeLine);

			if (ret.equals("success")) {
				prodApplications.setRegState(timeLine.getRegState());
				RetObject retObject2 = timelineService.saveTimeLine(timeLine);
				if (retObject2.getMsg().equals("persist")) {
					timeLine = (TimeLine) retObject2.getObj();
					processProdBn.setModerator(moderator);
					processProdBn.setTimeLine(timeLine);
					processProdBn.getTimeLineList().add(timeLine);
					processProdBn.setProdApplications(timeLine.getProdApplications());
					processProdBn.setProduct(timeLine.getProdApplications().getProduct());
					facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global.success")));
				} else {
					facesContext.addMessage(null, new FacesMessage(resourceBundle.getString("global_fail")));
				}
			} else {
				facesContext.addMessage(null, new FacesMessage("Please verify the dossier and update the checklist"));

			}

		}
		return "/internal/processprodlist";
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void prescreenfeerecvd() {
		processProdBn.getProdApplications().setPrescreenfeeReceived(true);
	}

	public String sendToApplicant() {
		processProdBn.save();
		//        TimeLine timeLine = getTimeLine();
		//        timeLine = new TimeLine();
		//        timeLine.setRegState(RegState.FOLLOW_UP);
		//        timeLine.setStatusDate(new Date());
		//        timeLine.setUser(userSession.getLoggedInUserObj());
		//        RetObject retObject = timelineService.saveTimeLine(timeLine);
		//        if (retObject.getMsg().equals("persist")) {
		//            timeLine = (TimeLine) retObject.getObj();
		//            processProdBn.setTimeLine(timeLine);
		//            processProdBn.getTimeLineList().add(timeLine);
		//            facesContext.addMessage(null, new FacesMessage(resourceBundle.getString(retObject.getMsg())));
		//        } else {
		//            facesContext.addMessage(null, new FacesMessage("Please verify the dossier and update the checklist"));
		//        }
		facesContext = FacesContext.getCurrentInstance();
		resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		RetObject retObject = prodAppChecklistService.saveProdAppChecklists(prodAppChecklists);
		prodAppChecklists = (List<ProdAppChecklist>) retObject.getObj();
		Flash flash = facesContext.getExternalContext().getFlash();
		flash.put("prodAppID", processProdBn.getProdApplications().getId());
		if (!retObject.getMsg().equals("persist")) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, retObject.getMsg(), retObject.getMsg()));
			return "";
		}
		return "deficiency";

	}

	public String archiveApp() {
		RetObject retObject = prodAppChecklistService.saveProdAppChecklists(prodAppChecklists);
		if (!retObject.getMsg().equals("persist")) {
			facesContext.addMessage(null, new FacesMessage(resourceBundle.getString(retObject.getMsg())));
			return "";
		}
		//        TimeLine timeLine = getTimeLine();
		//        timeLine = new TimeLine();
		timeLine.setRegState(RegState.DEFAULTED);
		timeLine.setStatusDate(new Date());
		timeLine.setUser(userService.findUser(userSession.getLoggedINUserID()));
		processProdBn.setTimeLine(timeLine);
		retObject = timelineService.saveTimeLine(timeLine);
		if (retObject.getMsg().equals("persist")) {
			timeLine = (TimeLine) retObject.getObj();
			processProdBn.setTimeLine(timeLine);
			processProdBn.getTimeLineList().add(timeLine);
			facesContext.addMessage(null, new FacesMessage(resourceBundle.getString(retObject.getMsg())));
		} else {
			facesContext.addMessage(null, new FacesMessage("Please verify the dossier and update the checklist"));
		}
		return "";

	}

	public void handleFileUpload(FileUploadEvent event) {
		file = event.getFile();
		FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, message);
		try {
			if (file != null) {
				setFileName(file.getFileName());
				prodAppChecklist.setFile(IOUtils.toByteArray(file.getInputstream()));
				prodAppChecklist.setFileName(file.getFileName());
				prodAppChecklist.setContentType(file.getContentType());
				prodAppChecklist.setUploadedBy(userService.findUser(userSession.getLoggedINUserID()));
				prodAppChecklist.setFileUploaded(true);
			} else {
				setFileName("");
				FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), resourceBundle.getString("upload_fail"));
				facesContext.addMessage(null, msg);

			}
		} catch (IOException e) {
			FacesMessage msg = new FacesMessage(resourceBundle.getString("global_fail"), file.getFileName() + resourceBundle.getString("upload_fail"));
			facesContext.addMessage(null, msg);
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public void addModuleDoc() {
		facesContext = FacesContext.getCurrentInstance();
		resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		RetObject retObject = prodAppChecklistService.saveProdAppChecklists(prodAppChecklists);
		prodAppChecklists = (List<ProdAppChecklist>) retObject.getObj();
		//                setChecklists(null);

	}


	public List<ProdAppChecklist> getProdAppChecklists() {
		if (prodAppChecklists == null && processProdBn.getProdApplications() != null) {
			prodAppChecklists = prodAppChecklistService.findProdAppChecklistByProdApp(processProdBn.getProdApplications().getId());
		}
		return prodAppChecklists;
	}

	public void setProdAppChecklists(List<ProdAppChecklist> prodAppChecklists) {
		this.prodAppChecklists = prodAppChecklists;
	}

	public ProdAppChecklist getProdAppChecklist() {
		return prodAppChecklist;
	}

	public void setProdAppChecklist(ProdAppChecklist

			prodAppChecklist) {
		//        processProdBn.setProdAppChecklist(prodAppChecklist);
		this.prodAppChecklist = prodAppChecklist;
	}
	public void initProdAppChecklist(ProdAppChecklist prodAppChecklist) {
		this.prodAppChecklist = prodAppChecklist;
		prodAppChecklistService.saveProdAppChecklists(prodAppChecklists);
	}

	public boolean isDisplayScreenAction() {
		if (processProdBn != null && processProdBn.getProdApplications() != null) {
			if (processProdBn.getProdApplications().getRegState().equals(RegState.NEW_APPL) || processProdBn.getProdApplications().getRegState().equals(RegState.FOLLOW_UP)
					|| processProdBn.getProdApplications().getRegState().equals(RegState.FEE) || processProdBn.getProdApplications().getRegState().equals(RegState.VERIFY))
				displayScreenAction = true;
			else
				displayScreenAction = false;
		}
		return displayScreenAction;
	}

	public void setDisplayScreenAction(boolean displayScreenAction) {
		this.displayScreenAction = displayScreenAction;
	}

	public TimelineService getTimelineService() {
		return timelineService;
	}

	public void setTimelineService(TimelineService timelineService) {
		this.timelineService = timelineService;
	}

	public ProcessProdBn getProcessProdBn() {
		return processProdBn;
	}

	public void setProcessProdBn(ProcessProdBn processProdBn) {
		this.processProdBn = processProdBn;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public ProdAppChecklistService getProdAppChecklistService() {
		return prodAppChecklistService;
	}

	public void setProdAppChecklistService(ProdAppChecklistService prodAppChecklistService) {
		this.prodAppChecklistService = prodAppChecklistService;
	}

	public TimeLine getTimeLine() {
		return timeLine;
	}

	public void setTimeLine(TimeLine timeLine) {
		this.timeLine = timeLine;
	}

	public User getModerator() {
		return moderator;
	}

	public void setModerator(User moderator) {
		this.moderator = moderator;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ProdApplicationsService getProdApplicationsService() {
		return prodApplicationsService;
	}

	public void setProdApplicationsService(ProdApplicationsService prodApplicationsService) {
		this.prodApplicationsService = prodApplicationsService;
	}

}
