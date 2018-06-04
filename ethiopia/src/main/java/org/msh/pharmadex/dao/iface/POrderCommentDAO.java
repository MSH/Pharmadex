package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.POrderChecklist;
import org.msh.pharmadex.domain.POrderComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by utkarsh on 12/19/14.
 */
public interface POrderCommentDAO extends JpaRepository<POrderComment, Long> {

    @Query("select c from POrderComment c left join fetch c.user where c.pipOrder.id = ?1")
    public List<POrderComment> findByPipOrder_Id(Long id);

    @Query("select c from POrderComment c left join fetch c.user where c.purOrder.id = ?1")
    public List<POrderComment> findByPurOrder_Id(Long id);
}
