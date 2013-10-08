package org.msh.pharmadex.domain;

import org.msh.pharmadex.domain.enums.RegState;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "error_log")
public class ErrorLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date errorDate;

    @Column(length = 100)
    private String browserName;

    @Column(length = 100)
    private String userIP;

    @Column(length = 100)
    private String requestURI;

    @Column(length = 100)
    private String ajaxRequest;

    @Column(length = 100)
    private String statusCode;

    @Column(length = 100)
    private String exceptionType;

    @Column(length = 100)
    private String exceptionMessage;

    @Column
    private String stackTrace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getAjaxRequest() {
        return ajaxRequest;
    }

    public void setAjaxRequest(String ajaxRequest) {
        this.ajaxRequest = ajaxRequest;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
