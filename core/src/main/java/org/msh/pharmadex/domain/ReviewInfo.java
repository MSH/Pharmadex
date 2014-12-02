/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RecomendType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Author: usrivastava
 */
@Entity
@Table(name = "review")
public class ReviewInfo implements Serializable {

    private static final long serialVersionUID = 4403558274641428489L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prod_app_id", nullable = false)
    private ProdApplications prodApplications;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private RecomendType recomendType;

    @Temporal(TemporalType.DATE)
    private Date assignDate;

    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @Lob
    @Column(nullable = true)
    private byte[] file;

    @OneToMany(mappedBy = "review", cascade = {CascadeType.ALL})
    private List<ReviewChecklist> reviewChecklists;

    @Transient
    private boolean submitted;


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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RecomendType getRecomendType() {
        return recomendType;
    }

    public void setRecomendType(RecomendType recomendType) {
        this.recomendType = recomendType;
    }

    public Date getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public List<ReviewChecklist> getReviewChecklists() {
        return reviewChecklists;
    }

    public void setReviewChecklists(List<ReviewChecklist> reviewChecklists) {
        this.reviewChecklists = reviewChecklists;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public boolean isSubmitted() {
        if (submitDate != null)
            submitted = true;
        return submitted;
    }


    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
