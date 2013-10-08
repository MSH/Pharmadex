package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
public class Comment implements Serializable
{
    private static final long serialVersionUID = 4518571160769966205L;
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 255)
	private String comment;

	private boolean internal;

	@ManyToOne
	@JoinColumn(name="PROD_ID", nullable = false)
    private ProdApplications prodApplications;

    @ManyToOne (fetch = FetchType.LAZY)
   	@JoinColumn(name="userId", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
   	@Column(name="comment_date")
   	private Date date;

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

    public ProdApplications getProdApplications() {
        return prodApplications;
    }

    public void setProdApplications(ProdApplications prodApplications) {
        this.prodApplications = prodApplications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
