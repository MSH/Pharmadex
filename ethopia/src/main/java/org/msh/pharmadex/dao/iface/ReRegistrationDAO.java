package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.processes.ReRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Одиссей on 21.06.2016.
 */
public interface ReRegistrationDAO extends JpaRepository<ReRegistration,Long> {

}