package org.msh.pharmadex.service;

import java.util.List;

/**
 * Created by utkarsh on 12/4/14.
 */
public class DisplayReviewInfo {

    private Long id;
    private Long reviewDetailID;
    private Long reviewInfoID;
    private String question;
    private boolean save;

    public DisplayReviewInfo(Long id, Long reviewDetailID, String question, boolean save, Long reviewInfoID) {
        this.id = id;
        this.question = question;
        this.save = save;
        this.reviewDetailID = reviewDetailID;
        this.reviewInfoID = reviewInfoID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public Long getReviewDetailID() {
        return reviewDetailID;
    }

    public void setReviewDetailID(Long reviewDetailID) {
        this.reviewDetailID = reviewDetailID;
    }

    public Long getReviewInfoID() {
        return reviewInfoID;
    }

    public void setReviewInfoID(Long reviewInfoID) {
        this.reviewInfoID = reviewInfoID;
    }
}
