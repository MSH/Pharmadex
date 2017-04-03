package org.msh.pharmadex.mbean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.dao.iface.WorkspaceDAO;
import org.msh.pharmadex.domain.Workspace;

/**

 */
@ManagedBean
@ViewScoped
public class WorkspaceMBean implements Serializable {
	
	@ManagedProperty(value = "#{workspaceDAO}")
	WorkspaceDAO workspaceDAO;

	FacesContext facesContext = FacesContext.getCurrentInstance();
	java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msgs");

	
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


}
