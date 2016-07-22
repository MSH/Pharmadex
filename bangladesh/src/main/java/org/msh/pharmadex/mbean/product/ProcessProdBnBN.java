package org.msh.pharmadex.mbean.product;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.ProdApplicationsServiceBN;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.RegistrationUtil;

import net.sf.jasperreports.engine.JRException;

/**
 * Backing bean to process the application made for registration
 */
@ManagedBean
@ViewScoped
public class ProcessProdBnBN implements Serializable {
	private static final long serialVersionUID = -7713990410626208287L;
	protected FacesContext facesContext = getCurrentInstance();
	protected java.util.ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

	@ManagedProperty(value = "#{prodApplicationsServiceBN}")
	protected ProdApplicationsServiceBN prodApplicationsServiceBN;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;
	
	@ManagedProperty(value = "#{processProdBn}")
    private ProcessProdBn processProdBn;

	public User loggedInUser;
	//private String gestorDeCTRM = resourceBundle.getString("gestorDeCTRM_value");
	
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

			String retValue = prodApplicationsServiceBN.registerProd(prodApplications);
			if(retValue.equals("created")) {
				prodApplicationsServiceBN.createRegCert(prodApplications, ""/*getGestorDeCTRM()*/);
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
	
	public ProdApplications findProdApplications() {
		return processProdBn.getProdApplications();
    }

	public ProdApplicationsServiceBN getProdApplicationsServiceBN(){
		return prodApplicationsServiceBN;
	}

	public void setProdApplicationsServiceBN(ProdApplicationsServiceBN service){
		this.prodApplicationsServiceBN = service;
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

	/*public String getGestorDeCTRM() {
		return gestorDeCTRM;
	}

	public void setGestorDeCTRM(String gestorDeCTRM) {
		this.gestorDeCTRM = gestorDeCTRM;
	}*/
    
}