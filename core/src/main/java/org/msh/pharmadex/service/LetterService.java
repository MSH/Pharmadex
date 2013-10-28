package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.LetterDAO;
import org.msh.pharmadex.domain.Letter;
import org.msh.pharmadex.domain.enums.LetterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
@Scope("singleton")
public class LetterService implements Serializable {


    @Autowired
    private LetterDAO letterDAO;

    private List<Letter> letters;


    public List<Letter> getLetters() {
//        if(letters==null)
        letters = (List<Letter>) letterDAO.findAll();
        return letters;
    }

    public Letter findByTitle(String title) {
        return letterDAO.findByTitle(title);
    }

    public Letter findByLetterType(LetterType type) {
        return letterDAO.findByLetterType(type);
    }

    public void setLetters(List<Letter> letters) {
        this.letters = letters;
    }

    public String addLetter(Letter selLetter) {
        letterDAO.save(selLetter);
        letters = null;
        return "persisted";
    }
}
