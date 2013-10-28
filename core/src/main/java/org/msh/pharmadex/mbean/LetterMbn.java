package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.Letter;
import org.msh.pharmadex.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@Component
@Scope("request")
public class LetterMbn implements Serializable {

    @Autowired
    LetterService letterService;

    private List<Letter> letters;
    private Letter selLetter = new Letter();

    FacesContext facesContext = FacesContext.getCurrentInstance();

    private Long selLetterId;


    public void addLetter() {
        if (selLetter == null)
            selLetter = new Letter();
        System.out.println("" + selLetter.getBody());
        String result = letterService.addLetter(selLetter);
        if (result.equalsIgnoreCase("persisted")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "" + selLetter.getTitle() + " was created successfully."));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "There was an error creating " + selLetter.getTitle()));
        }
        selLetter = new Letter();
    }

    public void cancel() {
        selLetter = new Letter();
    }


    public List<Letter> getLetters() {
        if (letters == null)
            letters = letterService.getLetters();
        return letters;
    }

    public void setLetters(List<Letter> letters) {
        this.letters = letters;
    }

    public Letter getSelLetter() {
        return selLetter;
    }

    public void setSelLetter(Letter selLetter) {
        this.selLetter = selLetter;
    }

    public Long getSelLetterId() {
        return selLetterId;
    }

    public void setSelLetterId(Long selLetterId) {
        this.selLetterId = selLetterId;
    }
}