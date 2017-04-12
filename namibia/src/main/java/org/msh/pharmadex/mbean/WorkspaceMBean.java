package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.Holidays;
import org.msh.pharmadex.domain.Workspace;
import org.msh.pharmadex.service.HolidayService;
import org.msh.pharmadex.service.HolidaysDTO;

/**

 */
@ManagedBean
@ViewScoped
public class WorkspaceMBean implements Serializable {

	private Date tmpDate = null;
	private String tmpString = null;
	private Integer tmpYear= null;

	@ManagedProperty(value = "#{workspaceDAO}")
	WorkspaceDAO workspaceDAO;

	@ManagedProperty(value="#{holidayService}")
	HolidayService holidayService;

	private Workspace ws = null;
	private boolean editws = false;

	public String exception() throws Exception {
		throw new Exception();
	}

	@PostConstruct
	private void init() {
		List<Workspace> list = workspaceDAO.findAll();
		if(list != null && list.size() > 0)
			ws = list.get(0);
	}

	public String update(){
		setEditws(true);
		return "";
	}

	public String save(){
		workspaceDAO.save(ws);
		setEditws(false);
		return "/admin/workspaceform.faces";
	}

	public String cancel(){

		return "";
	}


	public boolean isEditws() {
		return editws;
	}

	public void setEditws(boolean editws) {
		this.editws = editws;
	}

	public Workspace getWs() {
		return ws;
	}

	public void setWs(Workspace ws) {
		this.ws = ws;
	}

	public WorkspaceDAO getWorkspaceDAO() {
		return workspaceDAO;
	}

	public void setWorkspaceDAO(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}


	public HolidayService getHolidayService() {
		return holidayService;
	}

	public void setHolidayService(HolidayService holidayService) {
		this.holidayService = holidayService;
	}
	/**
	 * Buffer for date
	 * @return
	 */
	public Date getTmpDate() {
		return tmpDate;
	}

	public void setTmpDate(Date tmpDate) {
		this.tmpDate = tmpDate;
	}
	/**
	 * Buffer for strings
	 * @return
	 */
	public String getTmpString() {
		return tmpString;
	}

	public void setTmpString(String tmpString) {
		this.tmpString = tmpString;
	}


	public Integer getTmpYear() {
		return tmpYear;
	}

	public void setTmpYear(Integer tmpYear) {
		this.tmpYear = tmpYear;
	}

	/**
	 * Add to the database weekends for this year
	 */
	public void initWeekEnds(){
		Calendar cal = GregorianCalendar.getInstance();
		getHolidayService().createWeekends(cal.get(Calendar.YEAR), Calendar.SUNDAY, Calendar.SATURDAY);
	}
	/**
	 * Fetch data for the holidays table. Create weekends for this year if not exists
	 * @return
	 */
	public List<HolidaysDTO> fetchAllHolidays(){
		initWeekEnds(); //to ensure this year
		return getHolidayService().fetchHolidays();
	}

	/**
	 * Get min date for the holidays calendar
	 * @return
	 */
	public Date getMinDate(){
		Calendar cal = GregorianCalendar.getInstance();
		if(getTmpYear() != null){
			cal.set(getTmpYear(), 0, 1);
		}
		return cal.getTime();
	}
	public void setMinDate(Date d){
		//only for bean specific
	}
	/**
	 * Get min date for the holidays calendar
	 * @return
	 */
	public Date getMaxDate(){
		Calendar cal = GregorianCalendar.getInstance();
		if(getTmpYear() != null){
			cal.set(getTmpYear(), 11, 31);
		}
		return cal.getTime();
	}
	public void setMaxDate(Date d){
		//only for bean specific
	}
	/**
	 * Pass year for the dialog box
	 * @param year
	 */
	public void passYear(Integer year){
		setTmpYear(year);
	}


	/**
	 * Save a holiday record. If holiday is weekend - add only comment
	 */
	public void saveHoliday(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		if(getTmpDate() != null && getTmpString() != null){
			getHolidayService().addHoliday(getTmpDate(), getTmpString());
		}else{
			facesContext.addMessage(null, new FacesMessage(bundle.getString("Calendar.noholiday")));
		}
		setTmpDate(null);
		setTmpString(null);
	}
	/**
	 * Get special days for use to display in calendar using beforeShowDay feature
	 * @return array definition for Java Script
	 */
	public String getSpecialDays(){
		String retStr = "'1547-01-01'";
		if(getTmpYear()!=null){
			List<Holidays> holidays = getHolidayService().fetchHolidays(getTmpYear(), false);
			List<String> ret = new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(holidays != null){
				for(Holidays h : holidays){
					ret.add(sdf.format(h.getValue()));
				}
			}
			for(String s : ret){
				retStr=retStr+", '"+s+"'";
			}
		}
		return retStr; 
	}
	
	/**
	 * Fetch an event for display in UI
	 */
	public void fetchEventByDate(){
		if(getTmpDate() != null){
			Holidays holiday = getHolidayService().fetchHolidayByDate(getTmpDate());
			if(holiday!=null){
				setTmpString(holiday.getComment());
			}else{
				setTmpString(null);
			}
		}
	}
	/**
	 * Remove an event at date given
	 */
	public void removeEvent(){
		if(getTmpDate() != null && getTmpString()!=null){
			getHolidayService().removeHolidayByDate(getTmpDate());
			setTmpDate(null);
			setTmpString(null);
		}
	}

}
