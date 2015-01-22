package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by utkarsh on 1/7/15.
 * User: usrivastava
 * Date: 1/7/15
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sra")
public class SRA extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255)
    private String country;

    @Column(length = 255)
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
