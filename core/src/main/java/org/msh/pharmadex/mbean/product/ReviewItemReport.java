package org.msh.pharmadex.mbean.product;

import java.io.Serializable;

/**
 * Created by dudchenko
 */
public class ReviewItemReport implements Serializable {

	private Long detailId;
	private Long questionId;
    private String header1;
    private String header2;
    private String firstRevName;
    private String secondRevName;
    private String firstRevComment;
    private String secondRevComment;
    private String pages;
    private String reviewQuestion;
    private byte[] file;
    
	public Long getDetailId() {
		return detailId;
	}
	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	public String getHeader1() {
		return header1;
	}
	public void setHeader1(String header1) {
		this.header1 = header1;
	}
	public String getHeader2() {
		return header2;
	}
	public void setHeader2(String header2) {
		this.header2 = header2;
	}
	public String getFirstRevName() {
		return firstRevName;
	}
	public void setFirstRevName(String firstRevName) {
		this.firstRevName = firstRevName;
	}
	public String getSecondRevName() {
		return secondRevName;
	}
	public void setSecondRevName(String secondRevName) {
		this.secondRevName = secondRevName;
	}
	public String getFirstRevComment() {
		return firstRevComment;
	}
	public void setFirstRevComment(String firstRevComment) {
		this.firstRevComment = firstRevComment;
	}
	public String getSecondRevComment() {
		return secondRevComment;
	}
	public void setSecondRevComment(String secondRevComment) {
		this.secondRevComment = secondRevComment;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public String getReviewQuestion() {
		return reviewQuestion;
	}
	public void setReviewQuestion(String reviewQuestion) {
		this.reviewQuestion = reviewQuestion;
	}
    
	
}
