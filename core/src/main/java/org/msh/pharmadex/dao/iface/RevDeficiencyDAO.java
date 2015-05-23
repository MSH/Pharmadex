package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.domain.RevDeficiency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface RevDeficiencyDAO extends JpaRepository<RevDeficiency, Long> {

    public List<RevDeficiency> findByReviewInfo_Id(Long reviewInfo_Id);
}
