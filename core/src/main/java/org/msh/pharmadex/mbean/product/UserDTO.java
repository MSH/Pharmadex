package org.msh.pharmadex.mbean.product;

import org.msh.pharmadex.domain.Address;

/**
 * Created by utkarsh on 9/16/14.
 */
public class UserDTO {
    private Long userId;

    private String name;

    private String username;

    private String password;

    private String email;

    private String phoneNo;

    private String faxNo;

    private String comments;

    private Address address = new Address();


    public UserDTO(Long userId, String name, String username, String password, String email, String phoneNo) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public UserDTO(org.msh.pharmadex.domain.User u) {
        this.userId = u.getUserId();
        this.name = u.getName();
        this.username = u.getUsername();
        this.password = u.getPassword();
        this.email = u.getEmail();
        this.phoneNo = u.getPhoneNo();
        this.address = u.getAddress();
        this.faxNo = u.getFaxNo();

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
