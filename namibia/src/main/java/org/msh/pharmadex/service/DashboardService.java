package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.List;

import org.msh.pharmadex.dao.DashboardDAO;
import org.msh.pharmadex.mbean.ItemDashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: dudchenko
 */
@Service
public class DashboardService implements Serializable {

	private static final long serialVersionUID = 7163044735330114713L;

	@Autowired
	DashboardDAO dashboardDAO;
	
	public List<ItemDashboard> getListTimesProcess(){
		return dashboardDAO.getListTimesProcess();
	}
	
	public List<ItemDashboard> getListByPercentNemList(){
		return dashboardDAO.getListByPercentNemList();
	}
	
	public List<ItemDashboard> getListByPercentApprovedList(){
		return dashboardDAO.getListByPercentApprovedList();
	}
	
	public List<ItemDashboard> getListByApplicant(){
		return dashboardDAO.getListByApplicant();
	}
}
