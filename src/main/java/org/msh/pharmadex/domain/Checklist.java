package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "checklist")
public class Checklist extends CreationDetail implements Serializable {
    private static final long serialVersionUID = -6121362181619288650L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    private String name;

    private String module;

    private String moduleNo;

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

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModuleNo() {
        return moduleNo;
    }

    public void setModuleNo(String moduleNo) {
        this.moduleNo = moduleNo;
    }

}
