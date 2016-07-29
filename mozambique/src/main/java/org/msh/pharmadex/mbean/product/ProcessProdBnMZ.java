package org.msh.pharmadex.mbean.product;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.domain.enums.ReviewStatus;
import org.msh.pharmadex.service.ProdApplicationsServiceMZ;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RegistrationUtil;

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

	public String registerProduct(ProdApplications prodApplications) {
		facesContext = getCurrentInstance();
		try {
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
				//timeLineList = null;
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global.success"), resourceBundle.getString("status_change_success")));
			}else{
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resourceBundle.getString("global_fail"), "Error registering the product"));
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
		// timeLine = new TimeLine();
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
