package org.msh.pharmadex.domain.lab;


import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.SampleTestStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
public class SampleComment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 500)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "sampletest_ID", nullable = true)
    private SampleTest sampleTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "comment_date")
    private Date date;

    @Enumerated(EnumType.STRING)
    private SampleTestStatus sampleTestStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public SampleTest getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(SampleTest sampleTest) {
        this.sampleTest = sampleTest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SampleTestStatus getSampleTestStatus() {
        return sampleTestStatus;
    }

    public void setSampleTestStatus(SampleTestStatus sampleTestStatus) {
        this.sampleTestStatus = sampleTestStatus;
    }
}
