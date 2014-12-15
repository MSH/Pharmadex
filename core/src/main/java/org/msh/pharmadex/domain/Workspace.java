package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/14/12
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "workspace")
public class Workspace extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(length=10)
    private String defaultLocale;

    @Column
    private int prodRegDuration;

    @Column
    private boolean displatPricing;

    @Column
    private boolean detailReview;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDisplatPricing() {
        return displatPricing;
    }

    public void setDisplatPricing(boolean displatPricing) {
        this.displatPricing = displatPricing;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public int getProdRegDuration() {
        return prodRegDuration;
    }

    public void setProdRegDuration(int prodRegDuration) {
        this.prodRegDuration = prodRegDuration;
    }

    public boolean isDetailReview() {
        return detailReview;
    }

    public void setDetailReview(boolean detailReview) {
        this.detailReview = detailReview;
    }
}