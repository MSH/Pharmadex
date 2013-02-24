package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 2/17/13
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class WebSession {

    private User user;
    private Applicant applicant;
    private Product product;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
