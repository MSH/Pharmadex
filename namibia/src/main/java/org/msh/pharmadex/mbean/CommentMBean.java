package org.msh.pharmadex.mbean;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.msh.pharmadex.auth.UserSession;
import org.msh.pharmadex.domain.Comment;
import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.mbean.product.ProcessProdBn;
import org.msh.pharmadex.mbean.product.ReviewInfoBn;
import org.msh.pharmadex.service.CommentService;
import org.msh.pharmadex.service.UserService;




/**
 * @author Admin
 *
 */
@ManagedBean
@ViewScoped
public class CommentMBean implements Serializable {

	private FacesContext facesContext = FacesContext.getCurrentInstance();

	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;
	@ManagedProperty(value = "#{commentService}")
	public CommentService commentService;
	@ManagedProperty(value = "#{userService}")
	public UserService userService;
	//ReviewC

	@ManagedProperty(value = "#{processProdBn}")
	private ProcessProdBn processProdBn;
	@ManagedProperty(value = "#{reviewInfoBn}")
	private ReviewInfoBn reviewInfoBn;

	private ProdApplications prodApp;
	private ReviewInfo revInfo;
	private Long prodAppID;
	private List<CommentTable> allcomments;
	private String comment = "";
	private boolean revComment = false;
	private Comment commItem;
	private ReviewComment revcommItem;
	private User curUser = null;

	@PostConstruct
	private void init() {
		facesContext = getCurrentInstance();
		try {
			prodApp = getProcessProdBn().getProdApplications();
			revInfo = getReviewInfoBn().getReviewInfo();

			if(prodApp == null && revInfo != null){ // form reviewInfo
				revComment = true;
				prodApp = revInfo.getProdApplications();
			}

			if(userSession.getLoggedINUserID() != null)
				curUser = userService.findUser(userSession.getLoggedINUserID());

			if(prodApp != null){
				prodAppID = prodApp.getId();
				allcomments = commentService.findAllCommentsByApp(prodAppID);
				sortAllCommentsList();
			}else
				facesContext.addMessage(null, new FacesMessage("Not Find ProdApplications."));
		} catch (Exception ex) {
			facesContext.addMessage(null, new FacesMessage("Use the link within the system to access this page."));
		}
	}

	private void sortAllCommentsList(){
		if(allcomments != null){
			List<CommentTable> tmp = new ArrayList<CommentTable>();
			for(CommentTable ct : allcomments){
				if(ct != null){
					tmp.add(ct);
				}
			}
			Collections.sort(tmp, new Comparator<CommentTable>() {
				@Override
				public int compare(CommentTable o1, CommentTable o2) {
					Date d1 = o1.getDateCom();
					Date d2 = o2.getDateCom();
					if(d1 != null && d2 != null)
						return -d1.compareTo(d2);
					return 0;
				}
			});
			setAllcomments(tmp);
		}
	}

	public void addComment() {
		if(isRevComment()){
			revcommItem = new ReviewComment();
			revcommItem.setDate(new Date());
			revcommItem.setUser(curUser);
			revcommItem.setComment(vefirComment());
			revcommItem.setFinalSummary(false);
			revcommItem.setReviewInfo(revInfo);

			revcommItem = commentService.saveReviewComment(revcommItem);
			CommentTable item = createCommentTable(revcommItem);
			allcomments.add(item);
		}else{
			commItem = new Comment();
			commItem.setDate(new Date());
			commItem.setUser(curUser);
			commItem.setComment(vefirComment());
			commItem.setProdApplications(prodApp);

			commItem = commentService.saveComment(commItem);
			CommentTable item = createCommentTable(commItem);
			allcomments.add(item);
		}
	}

	private String vefirComment(){
		String res = comment;
		if(comment.length() > 450){
			res = comment.substring(0, 450);
		}
		comment = "";
		return res;
	}

	public boolean isRevComment() {
		return revComment;
	}

	public void setRevComment(boolean isRevComment) {
		this.revComment = isRevComment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<CommentTable> getAllcomments() {
		sortAllCommentsList();
		return allcomments;
	}
	public void setAllcomments(List<CommentTable> allcomments) {
		this.allcomments = allcomments;
	}

	public CommentService getCommentService() {
		return commentService;
	}
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public ProcessProdBn getProcessProdBn() {
		return processProdBn;
	}

	public void setProcessProdBn(ProcessProdBn processProdBn) {
		this.processProdBn = processProdBn;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}


	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ReviewInfoBn getReviewInfoBn() {
		return reviewInfoBn;
	}

	public void setReviewInfoBn(ReviewInfoBn reviewInfoBn) {
		this.reviewInfoBn = reviewInfoBn;
	}

	private CommentTable createCommentTable(Comment it){
		CommentTable item = new CommentTable(it.getDate(), it.getUser().getName(), it.getComment(), null);
		return item;
	}

	private CommentTable createCommentTable(ReviewComment it){
		String str = (it.getRecomendType() != null ? it.getRecomendType().getKey() : "");
		CommentTable item = new CommentTable(it.getDate(), it.getUser().getName(), it.getComment(), str);
		return item;
	}
}