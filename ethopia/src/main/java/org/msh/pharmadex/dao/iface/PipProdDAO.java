package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PIPProd;
import org.msh.pharmadex.domain.POrderBase;
import org.msh.pharmadex.domain.PurOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: usrivastava
 */
public interface PipProdDAO extends JpaRepository<PIPProd, Long> {

}
