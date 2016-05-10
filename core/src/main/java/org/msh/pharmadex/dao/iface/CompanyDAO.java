package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Company;
import org.msh.pharmadex.domain.Country;
import org.msh.pharmadex.domain.enums.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: usrivastava
 */
public interface CompanyDAO extends JpaRepository<Company, Long> {

}

