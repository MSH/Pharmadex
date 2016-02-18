package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Cacheable
@Table(name = "currency")
public class Currency extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = false)
    private String currName;

    @Column(length = 30, nullable = false)
    private String currCD;

    @Column(length = 10, nullable = false)
    private String currSym;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrName() {
        return currName;
    }

    public void setCurrName(String currName) {
        this.currName = currName;
    }

    public String getCurrCD() {
        return currCD;
    }

    public void setCurrCD(String currCD) {
        this.currCD = currCD;
    }

    public String getCurrSym() {
        return currSym;
    }

    public void setCurrSym(String currSym) {
        this.currSym = currSym;
    }
}
