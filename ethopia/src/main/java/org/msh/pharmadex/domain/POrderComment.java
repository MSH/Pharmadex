package org.msh.pharmadex.domain;


import org.msh.pharmadex.domain.enums.RecomendType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
public class POrderComment implements Serializable
{
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 500)
	private String comment;

	@ManyToOne
	@JoinColumn(name="piporder_ID", nullable = true)
    private PIPOrder pipOrder;

    @ManyToOne
    @JoinColumn(name="purorder_ID", nullable = true)
    private PurOrder purOrder;

    @ManyToOne (fetch = FetchType.LAZY)
   	@JoinColumn(name="userId", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
   	@Column(name="comment_date")
   	private Date date;

    @Enumerated(EnumType.STRING)
    private RecomendType recomendType;


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

    public PIPOrder getPipOrder() {
        return pipOrder;
    }

    public void setPipOrder(PIPOrder pipOrder) {
        this.pipOrder = pipOrder;
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
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

    public RecomendType getRecomendType() {
        return recomendType;
    }

    public void setRecomendType(RecomendType recomendType) {
        this.recomendType = recomendType;
    }
}
