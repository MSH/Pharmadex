package org.msh.pharmadex.domain;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "applicant_type")
public class ApplicantType extends CreationDetail implements Serializable {

    private static final long serialVersionUID = 587445708675464359L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    @Column(length = 250)
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
