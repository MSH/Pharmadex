package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.AgentType;
import org.msh.pharmadex.domain.enums.UserState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "lic_holder")
public class LicenseHolder extends CreationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name;

    @Embedded
    private Address address = new Address();

    @Column(length = 500)
    private String contactName;

    @Column(length = 500)
    private String phoneNo;

    @Column(length = 500)
    private String faxNo;

    @Column(length = 500)
    private String email;

    @Column(length = 500)
    private String website;

    @Column(length = 500)
    private String comment;

    @OneToOne
    private User createdBy;

    @Enumerated(EnumType.STRING)
    private UserState state;

    @OneToMany(mappedBy = "licenseHolder", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<AgentInfo> agentInfos;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name="licholder_prod",
            joinColumns = @JoinColumn( name="licholder_id"),
            inverseJoinColumns = @JoinColumn( name="prod_id")

    )
    private List<Product> products;

    @Transient
    private String firstAgent;

    @Transient
    private String secAgent;

    @Transient
    private String thirdAgent;

    public String getFirstAgent() {
        if (firstAgent == null) {
            initAgents();
        }
        return firstAgent;
    }

    public void setFirstAgent(String firstAgent) {
        this.firstAgent = firstAgent;
    }

    public String getSecAgent() {
        if (secAgent == null) {
            initAgents();
        }
        return secAgent;
    }

    public void setSecAgent(String secAgent) {
        this.secAgent = secAgent;
    }

    public String getThirdAgent() {
        if (thirdAgent == null) {
            initAgents();
        }
        return thirdAgent;
    }

    public void setThirdAgent(String thirdAgent) {
        this.thirdAgent = thirdAgent;
    }

    private void initAgents() {
        List<AgentInfo> agentInfos = getAgentInfos();
        Date currDate = new Date();
        firstAgent = "";
        secAgent = "";
        thirdAgent = "";
        for (AgentInfo agentInfo : agentInfos) {
            if (agentInfo.getAgentType().equals(AgentType.FIRST) && currDate.after(agentInfo.getStartDate()) && currDate.before(agentInfo.getEndDate())) {
                firstAgent = agentInfo.getApplicant().getAppName();
            }
            if (agentInfo.getAgentType().equals(AgentType.SECOND) && currDate.after(agentInfo.getStartDate()) && currDate.before(agentInfo.getEndDate())) {
                secAgent = agentInfo.getApplicant().getAppName();
            }
            if (agentInfo.getAgentType().equals(AgentType.THIRD) && currDate.after(agentInfo.getStartDate()) && currDate.before(agentInfo.getEndDate())) {
                thirdAgent = agentInfo.getApplicant().getAppName();
            }
        }
    }

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<AgentInfo> getAgentInfos() {
        return agentInfos;
    }

    public void setAgentInfos(List<AgentInfo> agentInfos) {
        this.agentInfos = agentInfos;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return getName();    //To change body of overridden methods use File | Settings | File Templates.
    }

}
