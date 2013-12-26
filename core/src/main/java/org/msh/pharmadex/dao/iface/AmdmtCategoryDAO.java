package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.AmdmtCategory;
import org.msh.pharmadex.domain.enums.AmdmtType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */

public interface AmdmtCategoryDAO extends JpaRepository<AmdmtCategory, Long> {

    public List<AmdmtCategory> findByAmdmtType(AmdmtType amdmtType);

    public List<AmdmtCategory> findByIdIn(List<String> id);


}

