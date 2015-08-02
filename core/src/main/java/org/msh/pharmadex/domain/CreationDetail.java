package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author usrivastava
 */
@MappedSuperclass
public abstract class CreationDetail implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @OneToOne
    private User updatedBy;

    @OneToOne
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @PreUpdate
    public void onUpdate() {
        setUpdatedDate(new Date());
    }

    @PrePersist
    public void onCreate() {
        setCreatedDate(new Date());
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "[createdDate:" + createdDate + " -- updatedDate:" + updatedDate;
    }

}

