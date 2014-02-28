package org.msh.pharmadex.auth;

import org.msh.pharmadex.domain.Applicant;
import org.msh.pharmadex.domain.ProdAppChecklist;
import org.msh.pharmadex.domain.Product;
import org.msh.pharmadex.domain.User;
import org.primefaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 2/17/13
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class WebSession implements Serializable {

    private static final long serialVersionUID = -8430895236272272144L;
    private User user;
    private Applicant applicant;
    private Product product;
    private UploadedFile file;
    private ProdAppChecklist prodAppChecklist;

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

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public ProdAppChecklist getProdAppChecklist() {
        return prodAppChecklist;
    }

    public void setProdAppChecklist(ProdAppChecklist prodAppChecklist) {
        this.prodAppChecklist = prodAppChecklist;
    }
}
