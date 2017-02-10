package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

/**
 * DTO by all comments
 * comment
 * reviewcomment
 * samplecomment
 * .....
 * @author dudchenko
 *
 */
public class CommentTable implements Serializable{
	
	private Date dateCom;
	private String userName;
	private String comment;
	/**
	 * by ReviewComment - recomendType
	 * by TimeLine - regStatus
	 */
	private String type = "";
	
	protected FacesContext facesContext;
	protected ResourceBundle resourceBundle;
	
	public CommentTable() {
		super();
		
		facesContext = FacesContext.getCurrentInstance();
		resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
	}
	
	public CommentTable(Date dateCom, String userName, String comment, String type) {
		super();
		facesContext = FacesContext.getCurrentInstance();
		resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		
		this.dateCom = dateCom;
		this.userName = userName;
		this.comment = comment;
		if(type != null && type.length() > 1)
			this.type = resourceBundle.getString(type);
	}
	
	public Date getDateCom() {
		return dateCom;
	}
	public void setDateCom(Date dateCom) {
		this.dateCom = dateCom;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}
	public void setType(String _type) {
		this.type = _type;
	}
}
