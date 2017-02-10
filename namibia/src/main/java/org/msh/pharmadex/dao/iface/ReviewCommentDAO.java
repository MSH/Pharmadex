package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: dudchenko
 */
public interface ReviewCommentDAO extends JpaRepository<ReviewComment, Long> {

}
