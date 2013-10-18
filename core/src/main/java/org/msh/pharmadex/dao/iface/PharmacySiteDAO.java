package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.PharmacySite;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.ApplicantState;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: usrivastava
 */
public interface PharmacySiteDAO extends CrudRepository<PharmacySite, Long> {

    public ArrayList<PharmacySite> findByState(ApplicantState state);

    public List<PharmacySite> findByStateAndUsers(ApplicantState state, List<User> users);

}
