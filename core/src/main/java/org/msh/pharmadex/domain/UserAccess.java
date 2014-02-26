package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "useraccess")
public class UserAccess implements Serializable {

    private static final long serialVersionUID = 1334532025677820075L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @Temporal(value = TemporalType.DATE)
    private Date loginDate;

    @Temporal(value = TemporalType.DATE)
    private Date logoutDate;

    @Column(length = 200)
    private String Application;

    @Column(length = 1000)
    private String IpAddress;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(Date logoutDate) {
        this.logoutDate = logoutDate;
    }

    public String getApplication() {
        return Application;
    }

    public void setApplication(String application) {
        Application = application;
    }

    public String getIpAddress() {
        return IpAddress;
    }

    public void setIpAddress(String ipAddress) {
        IpAddress = ipAddress;
    }
}
