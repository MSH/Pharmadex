package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.AmdmtCategory;
import org.msh.pharmadex.domain.enums.AmdmtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */

public interface AmdmtCategoryDAO extends JpaRepository<AmdmtCategory, Long> {

    @Query("select a from AmdmtCategory a where a.amdmtType = ?1")
    List<AmdmtCategory> findByAmdmtType(AmdmtType amdmtType);

    List<AmdmtCategory> findByIdIn(List<String> id);


}

