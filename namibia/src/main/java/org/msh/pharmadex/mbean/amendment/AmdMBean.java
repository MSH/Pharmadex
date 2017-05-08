package org.msh.pharmadex.mbean.amendment;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.AgentAgreement;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.service.ApplicantService;
/**
 * Common MBean for all amendments
 * @author Alex Kurasoff
 *
 */
@ManagedBean
@ViewScoped
public class AmdMBean implements Serializable {
	private static final long serialVersionUID = 3389355490139561377L;
	private ProdApplications prodApplication;
	@ManagedProperty(value = "#{userSession}")
	private UserSession userSession;
	@ManagedProperty(value = "#{applicantService}")
	ApplicantService applicantService;



	public ApplicantService getApplicantService() {
		return applicantService;
	}

	public void setApplicantService(ApplicantService applicantService) {
		this.applicantService = applicantService;
	}

	public ProdApplications getProdApplication() {
		return prodApplication;
	}

	public void setProdApplication(ProdApplications prodApplication) {
		this.prodApplication = prodApplication;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	/**
	 * Can current user issue an amendment for this application?
	 * @return
	 */
	public boolean isExecutor(){
		return getUserSession().isAdmin() || getUserSession().isStaff() || isApplicantUser() || isAgentResponsible();
	}

	/**
	 * Is current user responsible of an agent?
	 * @return
	 */
	private boolean isAgentResponsible() {
		if(getProdApplication() != null){
			String currentUser = getUserSession().getLoggedInUser();
			if (currentUser != null){
				List<AgentAgreement> agents = getApplicantService().fetchAgentAgreements(getProdApplication().getApplicant());
				if(agents != null){
					for(AgentAgreement agent : agents){
						if(agent.getActive() && agent.isValid()){
							if(agent.getAgent().getContactName().equalsIgnoreCase(currentUser)){
								return true;
							}
						}
					}
					return false;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * Is current user belong to the applicant
	 * @return
	 */
	private boolean isApplicantUser() {
		if(getProdApplication() != null){
			return getUserSession().getUserApplicantId() == getProdApplication().getApplicant().getApplcntId();
		}else{
			return false;
		}
	}

	public void setExecutor(boolean dummy){
		//obey specification
	}

}
