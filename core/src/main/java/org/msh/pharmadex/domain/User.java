package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.UserType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User: usrivastava
 */
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = {"username",
        "email"}))
public class User extends CreationDetail implements Serializable {

    private static final long serialVersionUID = 4655799301574468181L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 255, unique = true, nullable = false)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(nullable = false, length = 255, unique = false)
    private String email;

    @Column(length = 255)
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private UserType type;

    @Embedded
    private Address address = new Address();

    private boolean enabled;

    private String companyName;

    @Column(length = 255)
    private Locale language;

    @Column(length = 255)
    private TimeZone timeZone;

    @Column(length = 255)
    private String comments;

    @ManyToOne
    @JoinColumn(name = "applcntId")
    private Applicant applicant;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "roleId"))
    private List<Role> roles;

    private String faxNo;

    @ManyToMany(targetEntity = PharmacySite.class, fetch = FetchType.LAZY)
    @JoinTable(name = "pharmacy_site_user", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "site_id"))
    private List<PharmacySite> pharmacySites;


    public List<PharmacySite> getPharmacySites() {
        return pharmacySites;
    }

    public void setPharmacySites(List<PharmacySite> pharmacySites) {
        this.pharmacySites = pharmacySites;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFaxNo() {
        return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return getName();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
