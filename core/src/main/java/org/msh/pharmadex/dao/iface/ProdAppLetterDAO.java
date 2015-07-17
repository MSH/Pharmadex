package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.ProdAppLetter;
import org.msh.pharmadex.domain.enums.LetterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface ProdAppLetterDAO extends JpaRepository<ProdAppLetter, Long> {

    List<ProdAppLetter> findByProdApplications_Id(Long prodApplications_Id);

    List<ProdAppLetter> findByProdApplications_IdAndLetterType(Long prodApplications_Id, LetterType letterType);

    List<ProdAppLetter> findByReviewInfo_Id(Long reviewInfo_Id);
}
