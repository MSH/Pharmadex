package org.msh.pharmadex.domain;



import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "country")
public class Country extends CreationDetail implements Serializable {
    private static final long serialVersionUID = 3189657829743194443L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 30, nullable = false)
    private String countryName;

    @Column(length = 30, nullable = false)
    private String countryCD;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (!countryCD.equals(country.countryCD)) return false;
        if (!countryName.equals(country.countryName)) return false;
        if (!id.equals(country.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + countryName.hashCode();
        result = 31 * result + countryCD.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return countryName;
    }
}
