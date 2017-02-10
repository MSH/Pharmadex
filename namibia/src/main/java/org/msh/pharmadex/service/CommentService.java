package org.msh.pharmadex.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.dao.iface.CommentDAO;
import org.msh.pharmadex.dao.iface.ReviewCommentDAO;
import org.msh.pharmadex.dao.iface.ReviewInfoDAO;
import org.msh.pharmadex.dao.iface.SampleTestDAO;
import org.msh.pharmadex.dao.iface.TimelineDAO;
import org.msh.pharmadex.domain.Comment;
import org.msh.pharmadex.domain.ReviewComment;
import org.msh.pharmadex.domain.ReviewInfo;
import org.msh.pharmadex.domain.TimeLine;
import org.msh.pharmadex.domain.lab.SampleComment;
import org.msh.pharmadex.domain.lab.SampleTest;
import org.msh.pharmadex.mbean.CommentTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: usrivastava
 */
@Service
public class CommentService implements Serializable {
	private static final long serialVersionUID = 6683255939380544611L;

	@Autowired
	CommentDAO commentDAO;
	@Autowired
	ReviewInfoDAO reviewInfoDAO;
	@Autowired
	ReviewCommentDAO reviewCommentDAO;
	@Autowired
	TimelineDAO timelineDAO;
	@Autowired
	SampleTestDAO sampleTestDAO;
	@Resource
	ProdApplicationsDAO prodApplicationsDAO;

	List<Comment> comments;
	List<CommentTable> allComments;

	@Transactional
	public List<Comment> findAllCommentsByApp(Long prodApplications_Id, boolean company) {
		comments = commentDAO.findByProdApplications_IdAndInternalOrderByDateDesc(prodApplications_Id, company);
		return comments;
	}

	public Comment saveComment(Comment comment) {
		comments = null;
		return commentDAO.saveAndFlush(comment);
	}

	public String deleteComment(Comment comment) {
		commentDAO.delete(comment);
		comments = null;
		return "deleted";
	}
	
	public ReviewComment saveReviewComment(ReviewComment comment) {
		return reviewCommentDAO.saveAndFlush(comment);
	}

	public List<CommentTable> findAllCommentsByApp(Long prodApplications_Id) {
		allComments = new ArrayList<CommentTable>();
		CommentTable newCom = null;
		List<Comment> comms = commentDAO.findByProdApplications_IdOrderByDateDesc(prodApplications_Id);
		if(comms != null && comms.size() > 0){
			for(Comment com:comms){
				newCom = new CommentTable(com.getDate(), com.getUser().getName(), com.getComment(), null);
				allComments.add(newCom);
			}
		}
		
		// 
		List<ReviewInfo> revinfos = reviewInfoDAO.findByProdApplications_IdOrderByAssignDateAsc(prodApplications_Id);
		if(revinfos != null && revinfos.size() > 0){
			for(ReviewInfo info:revinfos){
				List<ReviewComment> list = reviewInfoDAO.findReviewComments(info.getId());
				if(list != null && list.size() > 0){
					for(ReviewComment rc:list){
						String str = (rc.getRecomendType() != null ? rc.getRecomendType().getKey() : "");
						newCom = new CommentTable(rc.getDate(), rc.getUser().getName(), rc.getComment(), str);
						allComments.add(newCom);
					}
				}
			}
		}
		//
		List<TimeLine> lines = timelineDAO.findByProdApplications_IdOrderByStatusDateDesc(prodApplications_Id);
		if(lines != null && lines.size() > 0){
			for(TimeLine tl:lines){
				if(tl.getComment() != null && tl.getComment().length() > 1){
					String str = (tl.getRegState() != null ? tl.getRegState().getKey() : "");
					newCom = new CommentTable(tl.getStatusDate(), tl.getUser().getName(), tl.getComment(), str);
					allComments.add(newCom);
				}
			}
		}

		//
		List<SampleTest> testes = sampleTestDAO.findByProdApplications_Id(prodApplications_Id);
		if(testes != null && testes.size() > 0){
			for(SampleTest test:testes){
				List<SampleComment> list = sampleTestDAO.findSampleCommentsBySampleTest_id(test.getId());
				if(list != null && list.size() > 0){
					for(SampleComment rc:list){
						newCom = new CommentTable(rc.getDate(), rc.getUser().getName(), rc.getComment(), null);
						allComments.add(newCom);
					}
				}
			}
		}
		
		return allComments;
	}

}
