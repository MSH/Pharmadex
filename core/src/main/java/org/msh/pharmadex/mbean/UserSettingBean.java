package org.msh.pharmadex.mbean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("session")
public class UserSettingBean implements Serializable {
    private static final long serialVersionUID = 3068896712735096922L;

    private String selection;
    private boolean preference=true;
    private boolean changePwd=false;
    private boolean language=false;

    // getters & setters

    public String active() {
        try{
       setPreference(selection.equals("preference"));
       setChangePwd(selection.equals("changePwd"));
       setLanguage(selection.equals("language"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "/secure/usersettings.faces";
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public boolean isPreference() {
        return preference;
    }

    public void setPreference(boolean preference) {
        this.preference = preference;
    }

    public boolean isChangePwd() {
        return changePwd;
    }

    public void setChangePwd(boolean changePwd) {
        this.changePwd = changePwd;
    }

    public boolean isLanguage() {
        return language;
    }

    public void setLanguage(boolean language) {
        this.language = language;
    }
}