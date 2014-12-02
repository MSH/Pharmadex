package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.CommentDAO;
import org.msh.pharmadex.domain.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class CommentService implements Serializable {
    private static final long serialVersionUID = 6683255939380544611L;

    @Autowired
    CommentDAO commentDAO;

    List<Comment> comments;

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
}
