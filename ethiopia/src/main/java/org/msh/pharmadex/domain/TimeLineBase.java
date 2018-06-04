package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RegState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "timeline")
public class TimeLineBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    protected Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected RegState regState;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    protected Date statusDate;

    @Column(length = 500)
    protected String comment;

    @Column(length = 40)
    protected String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegState getRegState() {
        return regState;
    }

    public void setRegState(RegState regState) {
        this.regState = regState;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
