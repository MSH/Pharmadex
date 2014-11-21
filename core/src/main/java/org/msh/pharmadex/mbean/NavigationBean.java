package org.msh.pharmadex.mbean;

import org.springframework.web.util.WebUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class NavigationBean implements Serializable {
    private static final long serialVersionUID = -1798717174751773194L;
    private String selection;
    private boolean adminhome = true;
    private boolean onlineusers = false;
    private boolean userslist = false;
    private boolean rolelist = false;
    private boolean loggedinuser = false;

    // getters & setters

    public void active() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        setAdminhome(selection.equals("adminhome"));
        setOnlineusers(selection.equals("onlineusers"));
        setUserslist(selection.equals("userslist"));
        setRolelist(selection.equals("rolelist"));
        setLoggedinuser(selection.equals("loggedinuser"));
    }

    public String regProductAction() {
        System.out.println("reached inside regProductAction");
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "regHomeMbean", null);
        return "/secure/prodreghome.faces";

    }

    public String regApplicantAction() {
        System.out.println("reached inside regApplicantAction");
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        WebUtils.setSessionAttribute(request, "applicantMBean", null);
        return "/secure/appregistration.faces";

    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public boolean isAdminhome() {
        return adminhome;
    }

    public void setAdminhome(boolean adminhome) {
        this.adminhome = adminhome;
    }

    public boolean isOnlineusers() {
        return onlineusers;
    }

    public void setOnlineusers(boolean onlineusers) {
        this.onlineusers = onlineusers;
    }

    public boolean isUserslist() {
        return userslist;
    }

    public void setUserslist(boolean userslist) {
        this.userslist = userslist;
    }

    public boolean isRolelist() {
        return rolelist;
    }

    public void setRolelist(boolean rolelist) {
        this.rolelist = rolelist;
    }

    public boolean isLoggedinuser() {
        return loggedinuser;
    }

    public void setLoggedinuser(boolean loggedinuser) {
        this.loggedinuser = loggedinuser;
    }
}