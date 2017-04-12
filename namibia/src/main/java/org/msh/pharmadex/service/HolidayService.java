package org.msh.pharmadex.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.msh.pharmadex.dao.iface.HolidaysDAO;
import org.msh.pharmadex.domain.Holidays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

/**
 * Manage holidays and weekends
 * @author Alex Kurasoff
 *
 */
@Service
public class HolidayService {
	@Autowired
	HolidaysDAO holidaysDAO;
	/**
	 * Fetch all holidays for a year given
	 * @param year 
	 * @param holidays only national holidays, otherwise all holidays and weekends
	 * @return
	 */
	public List<Holidays> fetchHolidays(int year, boolean holidays){
		Calendar from = GregorianCalendar.getInstance();
		Calendar to = GregorianCalendar.getInstance();
		from.set(year, 0, 1, 0, 0, 0);
		to.set(year, 11, 31, 0, 0, 0);
		List<Holidays> res = holidaysDAO.findByDates(from.getTime(), to.getTime());
		List<Holidays> ret = new ArrayList<Holidays>();
		if(holidays & res != null){
			for(Holidays h : res){
				if(h.getHoliday()){
					ret.add(h);
				}
			}
			return ret;
		}else{
			return res;
		}
	}
	/**
	 * Create weekends for a year given
	 * @param year
	 * @param firstDay - first weekend day, please use Calendar const, f.i. Calendar.SATURDAY
	 * @param secondDay - second weekend day, please use Calendar const, f.i. Calendar.SUNDAY 
	 */
	public void createWeekends(int year, int firstDay, int secondDay){
		//prepare 
		List<Holidays> weekEnds = new ArrayList<Holidays>();
		List<Holidays> dbList = fetchHolidays(year, false);
		List<String> dates = holidaysToStringList(dbList);
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(year, 0, 1, 0, 0, 0);
		while(cal.get(Calendar.YEAR)==year){
			if(cal.get(Calendar.DAY_OF_WEEK) == firstDay || cal.get(Calendar.DAY_OF_WEEK) == secondDay){
				if(!dates.contains(dateToString(cal))){
					weekEnds.add(Holidays.getInstance(cal.getTime(), false, ""));
				}
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		if(weekEnds.size()>0){
			holidaysDAO.save(weekEnds);
			holidaysDAO.flush();
		}
	}
	/**
	 * Represent holidays dates to list of strings for convenient search
	 * @param dbList
	 * @return
	 */
	private List<String> holidaysToStringList(List<Holidays> dbList) {
		Calendar cal = GregorianCalendar.getInstance();
		List<String> ret = new ArrayList<String>();
		if(dbList != null){
			for(Holidays h : dbList){
				cal.setTime(h.getValue());
				ret.add(dateToString(cal));
			}
		}
		return ret;
	}
	/**
	 * Simple utility to convert date to string repr
	 * @param cal
	 * @return
	 */
	private String dateToString(Calendar cal) {
		return cal.get(Calendar.YEAR)+""+cal.get(Calendar.MONTH)+""+cal.get(Calendar.DAY_OF_MONTH);
	}

	private int getYearFromDate(Date dt){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(dt);
		return cal.get(Calendar.YEAR);
	}
	/**
	 * Fetch holidays for table on workspaceform.xhtml
	 * @return
	 */
	public List<HolidaysDTO> fetchHolidays(){
		List<HolidaysDTO> ret = new ArrayList<HolidaysDTO>();
		List<Holidays> holidays = holidaysDAO.findAll();
		if (holidays != null){
			int year = getYearFromDate(holidays.get(0).getValue());
			int weekends=0;
			int publics = 0;
			for(Holidays h :holidays){
				int newYear = getYearFromDate(h.getValue());
				if(newYear == year){
					if(h.getHoliday()){
						publics++;
					}else{
						weekends++;
					}
				}else{
					ret.add(new HolidaysDTO(year, weekends, publics));
					year=newYear;
					weekends=0;
					if(h.getHoliday()){
						publics=1;
						weekends=0;
					}else{
						weekends=1;
						publics=0;
					}
				}
			}
			ret.add(new HolidaysDTO(year, weekends, publics));
		}
		return ret;
	}

	/**
	 * add holiday only if this holiday not on weekend or other holiday
	 * assume that weekends are already exists for holidayDate year
	 * @param holidayDate date
	 * @param holidayName name of the holiday
	 */
	public void addHoliday(Date holidayDate, String holidayName) {
		List<Holidays> dbList = fetchHolidays(getYearFromDate(holidayDate), false);
		List<String> dates = holidaysToStringList(dbList);
		Holidays thisHoliday = null;
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(holidayDate);
		int index = dates.indexOf(dateToString(cal));
		if(index>-1){
			thisHoliday = dbList.get(index);
			thisHoliday.setComment(holidayName);
		}else{
			thisHoliday= Holidays.getInstance(holidayDate, true, holidayName);
		}
		holidaysDAO.saveAndFlush(thisHoliday);
	}

	/**
	 * Fetch a holiday by a date given
	 * @param date
	 * @return
	 */
	public Holidays fetchHolidayByDate(Date date) {
		List<Holidays> dbList = fetchHolidays(getYearFromDate(date), false);
		List<String> dates = holidaysToStringList(dbList);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		int index = dates.indexOf(dateToString(cal));
		if(index>-1){
			return dbList.get(index);
		}else{
			return null;
		}
	}
	/**
	 * Remove a holiday by a date given
	 * @param date
	 */
	public void removeHolidayByDate(Date date) {
		Holidays holiday = fetchHolidayByDate(date);
		if(holiday != null){
			holidaysDAO.delete(holiday);
			holidaysDAO.flush();
		}

	}



}
