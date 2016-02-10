package org.msh.pharmadex.domain;


import org.msh.pharmadex.util.RegistrationUtil;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Cacheable
@Table(name = "country")
public class Country extends CreationDetail implements Serializable {
    private static final long serialVersionUID = 3189657829743194443L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = false)
    private String countryName;

    @Column(length = 30, nullable = false)
    private String countryCD;

    @Transient
    private String key;

    public String getKey() {
        if(countryName!=null&&!countryName.equalsIgnoreCase("")) {
            String delimiter = "_";
            key = this.getClass().getSimpleName() + delimiter + RegistrationUtil.formatString(getCountryName());
        }
        return key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCD() {
        return countryCD;
    }

    public void setCountryCD(String countryCD) {
        this.countryCD = countryCD;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Country) && (id != null)
                ? id.equals(((Country) other).id)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    @Override
    public String toString() {
        return countryName;
    }
}
