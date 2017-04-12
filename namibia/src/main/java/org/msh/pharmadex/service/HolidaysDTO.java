package org.msh.pharmadex.service;
/**
 * It is for display holidays table on workspaceform.xhtml
 * @author Alex Kurasoff
 *
 */
public class HolidaysDTO {
	Integer year;
	Integer weekends;
	Integer holidays;
	
	
	
	public HolidaysDTO(Integer year, Integer weekends, Integer holidays) {
		super();
		this.year = year;
		this.weekends = weekends;
		this.holidays = holidays;
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getWeekends() {
		return weekends;
	}
	public void setWeekends(Integer weekends) {
		this.weekends = weekends;
	}
	/**
	 * public holidays
	 * @return
	 */
	public Integer getHolidays() {
		return holidays;
	}
	/**
	 * public holidays
	 * @param holidays
	 */
	public void setHolidays(Integer holidays) {
		this.holidays = holidays;
	}
	
}
