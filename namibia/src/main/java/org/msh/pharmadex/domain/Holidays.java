package org.msh.pharmadex.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Cacheable
@Table(name = "holidays")
public class Holidays {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;
	@Column(unique = true)
	private Date value;
	
	private String comment;
	/**
	 * It is national holiday
	 */
	private Boolean holiday;
	
	/**
	 * Convenient method to create new instance
	 * @param eventDate
	 * @param national
	 * @param comment
	 * @return
	 */
	public static Holidays getInstance(Date eventDate, boolean national, String comment){
		Holidays ret = new Holidays();
		ret.setValue(eventDate);
		ret.setHoliday(national);
		ret.setComment(comment);
		return ret;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getValue() {
		return value;
	}
	public void setValue(Date value) {
		this.value = value;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Boolean getHoliday() {
		return holiday;
	}
	public void setHoliday(Boolean holiday) {
		this.holiday = holiday;
	}
	
	
}
