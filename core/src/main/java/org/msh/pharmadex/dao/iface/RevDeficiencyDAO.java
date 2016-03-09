package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.domain.RevDeficiency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface RevDeficiencyDAO extends JpaRepository<RevDeficiency, Long> {

    public List<RevDeficiency> findByReviewInfo_Id(Long reviewInfo_Id);

    @Query(" select rd from RevDeficiency rd left join  rd.reviewInfo ri left join ri.prodApplications pa where pa.id = ?1 and rd.resolved = false ")
    public List<RevDeficiency> findOpenRefDeficiency(Long prodAppId);

}
