package org.msh.pharmadex.mbean;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Comment;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.mbean.product.ReviewInfoBn;
import org.msh.pharmadex.service.CommentService;
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
		if(isReport_2()){
			list = dashboardService.getListByPercentNemList();
		}else if(isReport_3()){
			list = dashboardService.getListByPercentApprovedList();
		}
	}
	
	public String getTitle(){
		if(isReport_2())
			return resourceBundle.getString("title_perNemList");
		if(isReport_3())
			return resourceBundle.getString("title_perAppl");
		return "";
	}
	
	public boolean isReport_2(){
		return numreport.equals("2");
	}
	
	public boolean isReport_3(){
		return numreport.equals("3");
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