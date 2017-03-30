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
		if(isReport(1)){
			list = dashboardService.getListTimesProcess();
		}else if(isReport(2)){
			list = dashboardService.getListByPercentNemList();
		}else if(isReport(3)){
			list = dashboardService.getListByPercentApprovedList();
		}else if(isReport(4)){
			list = dashboardService.getListByApplicant();
		}else if(isReport(5)){
			list = dashboardService.getListByGenName();
		}
	}
	
	public String getTitle(){
		if(isReport(1))
			return resourceBundle.getString("title_times");
		if(isReport(2))
			return resourceBundle.getString("title_perNemList");
		if(isReport(3))
			return resourceBundle.getString("title_perAppl");
		if(isReport(4))
			return resourceBundle.getString("title_byApplicant");
		if(isReport(5))
			return resourceBundle.getString("title_generic");
		return "";
	}
	
	public boolean isReport(int n){
		return numreport.equals(String.valueOf(n));
	}
	
	public boolean renderedColumn(int numCol){
		if(isReport(1) && numCol <= 6) // 1-6
			return true;
		
		if(isReport(2) && numCol <= 5)
			return true;
			
		if(isReport(3) && numCol <= 7)
			return true;
		
		if(isReport(4) && numCol <= 4)
			return true;
		
		if(isReport(5) && numCol <= 3)
			return true;
		
		return false;
	}
	
	public String headerColumn(int numCol){
		if(isReport(1)){
			switch (numCol) {
			case 1:
				return resourceBundle.getString("col_year");
			case 2:
				return resourceBundle.getString("col_quarter");
			case 3:
				return resourceBundle.getString("col_total");
			case 4:
				return resourceBundle.getString("col_avgscreening");
			case 5:
				return resourceBundle.getString("col_avgreview");
			case 6:
				return resourceBundle.getString("col_avgtotal");
			}
		}
		if(isReport(2)){
			switch (numCol) {
			case 1:
				return resourceBundle.getString("col_year");
			case 2:
				return resourceBundle.getString("col_quarter");
			case 3:
				return resourceBundle.getString("col_total");
			case 4:
				return resourceBundle.getString("col_count");
			case 5:
				return resourceBundle.getString("col_percent");
			}
		}
		if(isReport(3)){
			switch (numCol) {
			case 1:
				return resourceBundle.getString("col_year");
			case 2:
				return resourceBundle.getString("col_quarter");
			case 3:
				return resourceBundle.getString("col_total");
			case 4:
				return resourceBundle.getString("col_countReg");
			case 5:
				return resourceBundle.getString("col_percReg");
			case 6:
				return resourceBundle.getString("col_countRej");
			case 7:
				return resourceBundle.getString("col_percRej");
			}
		}
		if(isReport(4)){
			switch (numCol) {
			case 1:
				return resourceBundle.getString("col_appName");
			case 2:
				return resourceBundle.getString("col_regInNemList");
			case 3:
				return resourceBundle.getString("col_other");
			case 4:
				return resourceBundle.getString("col_total");
			}
		}
		if(isReport(5)){
			switch (numCol) {
			case 1:
				return resourceBundle.getString("col_genname");
			case 2:
				return resourceBundle.getString("col_nemlist");
			case 3:
				return resourceBundle.getString("col_total_1");
			}
		}

		return "";
	}
	
	public String valueColumn(int numCol, ItemDashboard item){
		if(item == null)
			return "";
		
		if(isReport(1)){
			switch (numCol) {
			case 1:
				return String.valueOf(item.getYear());
			case 2:
				return String.valueOf(item.getQuarter());
			case 3:
				return String.valueOf(item.getTotal());
			case 4:
				return String.valueOf(item.getAvg_screening());
			case 5:
				return String.valueOf(item.getAvg_review());
			case 6:
				return String.valueOf(item.getAvg_total());
			}
		}
		if(isReport(2)){
			switch (numCol) {
			case 1:
				return String.valueOf(item.getYear());
			case 2:
				return String.valueOf(item.getQuarter());
			case 3:
				return String.valueOf(item.getTotal());
			case 4:
				return String.valueOf(item.getCount());
			case 5:
				return String.valueOf(item.getPercent());
			}
		}
		if(isReport(3)){
			switch (numCol) {
			case 1:
				return String.valueOf(item.getYear());
			case 2:
				return String.valueOf(item.getQuarter());
			case 3:
				return String.valueOf(item.getTotal());
			case 4:
				return String.valueOf(item.getCount());
			case 5:
				return String.valueOf(item.getPercent());
			case 6:
				return String.valueOf(item.getCount_other());
			case 7:
				return String.valueOf(item.getPercent_other());
			}
		}
		if(isReport(4)){
			switch (numCol) {
			case 1:
				return String.valueOf(item.getName());
			case 2:
				return String.valueOf(item.getCount());
			case 3:
				return String.valueOf(item.getCount_other());
			case 4:
				return String.valueOf(item.getTotal());
			}
		}
		if(isReport(5)){
			switch (numCol) {
			case 1:
				return String.valueOf(item.getName());
			case 2:
				return String.valueOf(item.getCount());
			case 3:
				return String.valueOf(item.getCount_other());
			}
		}
		return "";
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
	
	/*
                <p:column headerText="#{msgs.col_appName}" rendered="#{dashboardMBean.report}">
                    <h:outputText value="#{item.name}"/>
                </p:column>
                <p:column headerText="#{msgs.col_regInNemList}" rendered="#{dashboardMBean.report}">
                    <h:outputText value="#{item.count}"/>
                </p:column>
                <p:column headerText="#{msgs.col_other}" rendered="#{dashboardMBean.report}">
                    <h:outputText value="#{item.count_other}"/>
                </p:column>
                <p:column headerText="#{msgs.col_total}" rendered="#{dashboardMBean.report}">
                    <h:outputText value="#{item.total}"/>
                </p:column>
	 */
}