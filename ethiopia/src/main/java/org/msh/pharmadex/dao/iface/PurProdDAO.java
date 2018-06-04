package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PIPProd;
import org.msh.pharmadex.domain.PurProd;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: usrivastava
 */
public interface PurProdDAO extends JpaRepository<PurProd, Long> {

}
