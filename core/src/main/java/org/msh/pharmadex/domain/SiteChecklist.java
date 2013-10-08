package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sitechecklist")
public class SiteChecklist extends CreationDetail implements Serializable {

    private static final long serialVersionUID = -5187124707987238485L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
