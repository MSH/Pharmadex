package org.msh.pharmadex.mbean;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.service.DashboardService;
import org.msh.pharmadex.service.UserService;
import org.msh.pharmadex.util.Scrooge;

/**
 * @author dudchenko
 *
 */
@ManagedBean
@ViewScoped
public class DashboardMBean implements Serializable {

	private static final long serialVersionUID = 4866135420621315047L;
	
	private FacesContext facesContext = FacesContext.getCurrentInstance();
	protected ResourceBundle resourceBundle;
	
	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;
	@ManagedProperty(value = "#{userService}")
	public UserService userService;
	@ManagedProperty(value = "#{dashboardService}")
	public DashboardService dashboardService;

	private List<ItemDashboard> list;
	
	private String numreport = "";
	
	@PostConstruct
	private void init() {
		facesContext = getCurrentInstance();
		resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		
		numreport = Scrooge.beanStrParam("numreport");
	}

	public void loadList(){
		if(isReport_1()){
			list = dashboardService.getListTimesProcess();
		}else if(isReport_2()){
			list = dashboardService.getListByPercentNemList();
		}else if(isReport_3()){
			list = dashboardService.getListByPercentApprovedList();
		}else if(isReport_4()){
			list = dashboardService.getListByApplicant();
		}
	}
	
	public String getTitle(){
		if(isReport_1())
			return resourceBundle.getString("title_times");
		if(isReport_2())
			return resourceBundle.getString("title_perNemList");
		if(isReport_3())
			return resourceBundle.getString("title_perAppl");
		if(isReport_4())
			return resourceBundle.getString("title_byApplicant");
		return "";
	}
	
	public boolean isReport_1(){
		return numreport.equals("1");
	}
	
	public boolean isReport_2(){
		return numreport.equals("2");
	}
	
	public boolean isReport_3(){
		return numreport.equals("3");
	}
	
	public boolean isReport_4(){
		return numreport.equals("4");
	}
	
	public List<ItemDashboard> getList() {
		return list;
	}
	public void setList(List<ItemDashboard> list) {
		this.list = list;
	}

	public DashboardService getDashboardService() {
		return dashboardService;
	}


	public void setDashboardService(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
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
	
	
}