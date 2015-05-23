package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Attachment;
import org.msh.pharmadex.domain.ProdAppLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface ProdAppLetterDAO extends JpaRepository<ProdAppLetter, Long> {

    public List<ProdAppLetter> findByProdApplications_Id(Long prodApplications_Id);

    public List<ProdAppLetter> findByReviewInfo_Id(Long reviewInfo_Id);
}
