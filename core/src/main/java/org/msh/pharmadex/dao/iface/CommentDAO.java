package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface CommentDAO extends JpaRepository<Comment, Long> {

    public List<Comment> findByProdApplications_IdOrderByDateDesc(Long prodApplications_Id);

    public List<Comment> findByProdApplications_IdAndInternalOrderByDateDesc(Long prodApplications_Id, boolean internal);
}
