package org.msh.pharmadex.domain;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * @author usrivastava
 */
@Embeddable
public class Address implements Serializable{
    private static final long serialVersionUID = 5188310112489422198L;
    @Column(length = 200)
   	private String address1;

   	@Column(length = 200)
   	private String address2;

   	@Column(length = 200)
   	private String zipcode;

   @OneToOne
   @JoinColumn(name="CNTRY_ID")
   	private Country country = new Country();

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}

