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
	
	private double avg_screening = 0;
	private double avg_review = 0;
	private double avg_total = 0;
	
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
			percent = round(val, 2);
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
			percent_other = round(val, 2);
		}
		
		return percent_other;
	}
	public void setPercent_other(double percent_other) {
		this.percent_other = percent_other;
	}
	
	public double getAvg_screening() {
		avg_screening = round(avg_screening, 2);
		return avg_screening;
	}
	public void setAvg_screening(double avg_screening) {
		this.avg_screening = avg_screening;
	}
	
	public double getAvg_review() {
		avg_review = round(avg_review, 2);
		return avg_review;
	}
	public void setAvg_review(double avg_review) {
		this.avg_review = avg_review;
	}
	
	public double getAvg_total() {
		avg_total = round(avg_total, 2);
		return avg_total;
	}
	public void setAvg_total(double avg_total) {
		this.avg_total = avg_total;
	}
	
	
	public double round(double val, int col){
		double value = new BigDecimal(val).setScale(col, RoundingMode.UP).doubleValue();
		return value;
	}
}
