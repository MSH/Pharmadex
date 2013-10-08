package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "status_user")
public class StatusUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    @OneToOne
    @JoinColumn(name = "module1")
    private User module1;

    private String review1;

    private String review2;

    private String review3;

    private String review4;


    @OneToOne
    @JoinColumn(name = "module2")
    private User module2;

    @OneToOne
    @JoinColumn(name = "module3")
    private User module3;

    @OneToOne
    @JoinColumn(name = "module4")
    private User module4;

    @Temporal(TemporalType.DATE)
    private Date assignDate;

    @Temporal(TemporalType.DATE)
    private Date module1SubmitDt;

    @Temporal(TemporalType.DATE)
    private Date module2SubmitDt;

    @Temporal(TemporalType.DATE)
    private Date module3SubmitDt;

    @Temporal(TemporalType.DATE)
    private Date module4SubmitDt;


    private boolean complete;

    @OneToOne
    private User uploadedBy;

    public StatusUser(ProdApplications prodApplications, Date date) {
        this.prodApplications = prodApplications;
        this.assignDate = date;
    }

    public StatusUser() {

    }

    public StatusUser(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public User getModule1() {
        return module1;
    }

    public void setModule1(User module1) {
        this.module1 = module1;
    }

    public User getModule2() {
        return module2;
    }

    public void setModule2(User module2) {
        this.module2 = module2;
    }

    public User getModule3() {
        return module3;
    }

    public void setModule3(User module3) {
        this.module3 = module3;
    }

    public User getModule4() {
        return module4;
    }

    public void setModule4(User module4) {
        this.module4 = module4;
    }

    public Date getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Date getModule1SubmitDt() {
        return module1SubmitDt;
    }

    public void setModule1SubmitDt(Date module1SubmitDt) {
        this.module1SubmitDt = module1SubmitDt;
    }

    public Date getModule2SubmitDt() {
        return module2SubmitDt;
    }

    public void setModule2SubmitDt(Date module2SubmitDt) {
        this.module2SubmitDt = module2SubmitDt;
    }

    public Date getModule3SubmitDt() {
        return module3SubmitDt;
    }

    public void setModule3SubmitDt(Date module3SubmitDt) {
        this.module3SubmitDt = module3SubmitDt;
    }

    public Date getModule4SubmitDt() {
        return module4SubmitDt;
    }

    public void setModule4SubmitDt(Date module4SubmitDt) {
        this.module4SubmitDt = module4SubmitDt;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getReview1() {
        return review1;
    }

    public void setReview1(String review1) {
        this.review1 = review1;
    }

    public String getReview2() {
        return review2;
    }

    public void setReview2(String review2) {
        this.review2 = review2;
    }

    public String getReview3() {
        return review3;
    }

    public void setReview3(String review3) {
        this.review3 = review3;
    }

    public String getReview4() {
        return review4;
    }

    public void setReview4(String review4) {
        this.review4 = review4;
    }
}
