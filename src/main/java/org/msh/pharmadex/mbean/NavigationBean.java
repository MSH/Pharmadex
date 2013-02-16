package org.msh.pharmadex.mbean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("session")
public class NavigationBean implements Serializable {
    private static final long serialVersionUID = -1798717174751773194L;
    private String selection;
    private boolean adminhome=true;
    private boolean onlineusers=false;
    private boolean userslist=false;
    private boolean rolelist=false;
    private boolean loggedinuser=false;

    // getters & setters

    public void active() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
       setAdminhome(selection.equals("adminhome"));
       setOnlineusers(selection.equals("onlineusers"));
       setUserslist(selection.equals("userslist"));
       setRolelist(selection.equals("rolelist"));
       setLoggedinuser(selection.equals("loggedinuser"));
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