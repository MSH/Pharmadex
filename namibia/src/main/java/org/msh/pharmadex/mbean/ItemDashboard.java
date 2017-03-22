package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemDashboard implements Serializable{

	private static final long serialVersionUID = -435959110097842685L;
	
	private int year = 0;
	private int quarter = 0;
	private int total = 0;
	private int count = 0;
	private double percent = 0.00;
	
	private int count_other = 0;// for report 3 by Reject
	private double percent_other = 0;// for report 3 by Reject
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getQuarter() {
		return quarter;
	}
	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public double getPercent() {
		percent = 0;
		if(total > 0 && count >= 0){
			double val = count*100.00/total;
			percent = new BigDecimal(val).setScale(2, RoundingMode.UP).doubleValue();
		}
		
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	
	public int getCount_other() {
		return count_other;
	}
	public void setCount_other(int count_other) {
		this.count_other = count_other;
	}
	
	public double getPercent_other() {
		percent_other = 0;
		if(total > 0 && count_other >= 0){
			double val = count_other*100.00/total;
			percent_other = new BigDecimal(val).setScale(2, RoundingMode.UP).doubleValue();
		}
		
		return percent_other;
	}
	public void setPercent_other(double percent_other) {
		this.percent_other = percent_other;
	}
	
	
}
